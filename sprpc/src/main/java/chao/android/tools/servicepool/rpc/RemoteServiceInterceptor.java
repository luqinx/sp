package chao.android.tools.servicepool.rpc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import chao.android.tools.servicepool.AndroidServicePool;
import chao.android.tools.servicepool.rpc.annotation.RemoteServiceConfig;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceInterceptor;
import chao.java.tools.servicepool.IServiceInterceptorCallback;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-07-22
 */
@Service(scope = ServicePool.SCOPE_GLOBAL)
public class RemoteServiceInterceptor implements IServiceInterceptor {

    private static final int USELESS_REMOTE_TIMEOUT = 5000; //5秒内不会重复去唤起同一个未安装的远程service

    private ConcurrentHashMap<Integer, String> uselessRemotes = new ConcurrentHashMap<>();

    private Map<String, RemoteClient> clientCache = new ConcurrentHashMap<>();

    private RemoteHandler mHandler;

    @Override
    public void intercept(Class<? extends IService> originClass, IService source, Method method, Object[] args, IServiceInterceptorCallback callback) {
        if (source == null) {
            callback.onContinue(method, args);
            return;
        }

        // 非RemoteService
        if (!(source instanceof RemoteService)) {
            callback.onContinue(method, args);
            return;
        }

        RemoteServiceConfig remoteServiceConfig = originClass.getAnnotation(RemoteServiceConfig.class);
        if (remoteServiceConfig == null) {
            callback.onContinue(method, args);
            return;
        }

        String packageName = remoteServiceConfig.remotePackageName();


        if (method.getDeclaringClass() == RemoteService.class
                && "remoteExist".equals(method.getName())) {
            callback.onInterrupt(RemoteUtil.remoteExist(packageName));
            return;
        }

        //忽略同进程下的拦截, 否则可能导致循环拦截
        if (AndroidServicePool.getContext().getPackageName().equals(packageName)) {
            callback.onContinue(method, args);
            return;
        }

        if (!remoteServiceConfig.forceMainThread() && RemoteUtil.inMainThread()) {
            throw new RemoteServiceException("remote service should not call in main thread!");
        }

        if (mHandler == null) {
            synchronized (this) {
                if (mHandler == null) {
                    mHandler = new RemoteHandler();
                }
            }
            mHandler = new RemoteHandler();
        }

        final String componentName = remoteServiceConfig.remoteComponentName();

        RemoteClient client = clientCache.get(componentName);

        int callId = RemoteUtil.checkAndHashMethod(method) ^ Thread.currentThread().hashCode();

        RemoteClientMethod clientMethod = new RemoteClientMethod(method, args, callback);


        if (client == null) {
            client = new RemoteClient(originClass, componentName);
            client.addMethod(callId, clientMethod);
            if (uselessRemotes.get(componentName.hashCode()) == null) { // 5秒之前没有唤起过这个远程service
                if (bindRemoteService(client, remoteServiceConfig, method, args, callId, callback)) {
                    // 非主线程时等待
                    if (!RemoteUtil.inMainThread()) {
                        client.awaitMethod(callId, remoteServiceConfig.timeout());
                    } else {
                        // onServiceConnected在主线程回调，这里是主线程时，等待会导致ServiceConnection无法接收到onServiceConnected消息
                    }
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            uselessRemotes.put(componentName.hashCode(), componentName); //缓存唤起远程失败的远程服务，5秒内不再重复唤起
                        }
                    }); //put和remove操作抛到同一个线程处理， 避免线程不安全问题
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            uselessRemotes.remove(componentName.hashCode());
                        }
                    }, USELESS_REMOTE_TIMEOUT);
                }
            } else {
                // pass
            }
        } else {
            client.addMethod(callId, clientMethod);
            sendMessage(client, method, args, callId);
            client.awaitMethod(callId, remoteServiceConfig.timeout());
        }

        if (!client.isMethodCountDown(callId)) {
            callback.onInterrupt(null);
        }

        client.removeMethod(callId);
//        client.printCache();
    }

    private void sendMessage(final RemoteClient client, final Method method, final Object[] args, int callId) {
        try {
            client.sendMessage(method, args, callId);
        } catch (RemoteException e) {
            e.printStackTrace();
            clientCache.remove(client.getComponentName());
            client.shutdown();
        }

    }


    private boolean bindRemoteService(final RemoteClient client, final RemoteServiceConfig remoteServiceConfig, final Method method, final Object[] args, final int callId, final IServiceInterceptorCallback callback) {
        Intent intent = new Intent();
        final String componentName = remoteServiceConfig.remoteComponentName();
        intent.setComponent(new ComponentName(remoteServiceConfig.remotePackageName(), componentName));
        return AndroidServicePool.getContext().bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                client.bindRemote(service);
                clientCache.put(componentName, client);
                sendMessage(client, method, args, callId);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                RemoteClient client = clientCache.get(componentName);
                if (client != null) {
                    client.shutdown();
                }
                clientCache.remove(componentName);
            }
        }, RemoteMessageService.BIND_AUTO_CREATE | RemoteMessageService.BIND_NOT_FOREGROUND);

    }
}
