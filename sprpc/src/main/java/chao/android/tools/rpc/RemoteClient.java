package chao.android.tools.rpc;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

import chao.java.tools.servicepool.ClassTypeAdapter;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.ServicePool;

/**
 * @author luqin
 * @since 2020-07-22
 */
public class RemoteClient implements Handler.Callback {

    private static final String TAG = "RemoteClient";

    static final String REMOTE_KEY_METHOD_NAME = "remote.method.name";

    static final String REMOTE_KEY_METHOD_HASH = "remote.method.hash";

    static final String REMOTE_KEY_ORIGIN_CLASS = "remote.origin.class";

    static final String REMOTE_KEY_ARGS = "remote.args";

    static final String REMOTE_KEY_ARG_TYPES = "remote.args.type";

    static final String REMOTE_KEY_RETURN = "remote.return";

    static final String REMOTE_KEY_RETURN_TYPE = "remote.return.type";

    private Messenger sendMessenger;

    private Messenger receiveMessenger;


    private String componentName;

    private Gson gson;

    private final ConcurrentHashMap<Integer, RemoteClientMethod> clientMethods;

    private RemoteHandler mHandler = new RemoteHandler(this);

    public RemoteClient(String componentName) {
        this.componentName = componentName;
        this.receiveMessenger = new Messenger(mHandler);
        clientMethods = new ConcurrentHashMap<>();
        gson = new GsonBuilder()
                .registerTypeAdapter(Class.class, new ClassTypeAdapter())
//                .registerTypeAdapter(Class[].class, new ClassArrayTypeAdapter())
                .create();
    }

    public void bindRemote(IBinder service) {
        this.sendMessenger = new Messenger(service);
    }



    public void sendMessage(Class<? extends IService> originClass, Method method, Object[] args, int callId) throws RemoteException {

        int methodHash = RemoteUtil.checkAndHashMethod(method);


        Message message = Message.obtain(mHandler, callId);
        Bundle bundle = new Bundle();

        bundle.putString(REMOTE_KEY_METHOD_NAME, method.getName());

        bundle.putString(REMOTE_KEY_ORIGIN_CLASS, originClass.getName());

        bundle.putString(REMOTE_KEY_ARGS, gson.toJson(args));

        bundle.putString(REMOTE_KEY_ARG_TYPES, gson.toJsonTree(method.getParameterTypes()).getAsJsonArray().toString());

        bundle.putInt(REMOTE_KEY_METHOD_HASH, methodHash);

        message.replyTo = receiveMessenger;
        message.setData(bundle);
        sendMessenger.send(message);
    }

    @Override
    public boolean handleMessage(Message msg) {
//        System.out.println("handleMessage in thread: " + Thread.currentThread().getName() + ", " + msg.what);

        int callId = msg.what;
        Bundle returnData = msg.getData();
        String returnType = returnData.getString(REMOTE_KEY_RETURN_TYPE);
        String returnJson = returnData.getString(REMOTE_KEY_RETURN);
        int methodHash = returnData.getInt(REMOTE_KEY_METHOD_HASH);

        RemoteClientMethod clientMethod = clientMethods.get(callId);
        if (clientMethod == null) {
            ServicePool.logger.e(TAG,"remote call return, but method hash not matches or timeout" );
            return false;
        }

        Method currentMethod = clientMethod.method;

        //check the hash
        if (methodHash != RemoteUtil.checkAndHashMethod(currentMethod)) {
            return false;
        }

        Object returnObject = null;
        if (Throwable.class.getName().equals(returnType)) {
            Throwable e = gson.fromJson(returnJson, Throwable.class);
            ServicePool.logger.e(TAG, "receive exception: " + e.getMessage());
            clientMethod.callback.onInterrupt(null);
            clientMethod.countDown();
            return true;
        }

        //check the return type
        if (returnJson != null) {

            if (clientMethod.callbackHandler != null) {
                clientMethod.callbackHandler.resolve(gson.fromJson(returnJson, clientMethod.callbackResolveType));
            }

            Type realReturnType = currentMethod.getReturnType();

            if (realReturnType == Void.class || realReturnType == void.class) {
                clientMethod.callback.onInterrupt(null);
                clientMethod.countDown();
                return false;
            }
            returnObject = gson.fromJson(returnJson, realReturnType);
        }

        clientMethod.callback.onInterrupt(returnObject);
        clientMethod.countDown();
        return true;
    }

    public String getComponentName() {
        return componentName;
    }

    public void shutdown() {
        for (RemoteClientMethod method: clientMethods.values()) {
            method.callback.onInterrupt(null);
            method.countDown();
        }
        clientMethods.clear();
    }

    public void countDownMethod(int callId) {
        RemoteClientMethod clientMethod = clientMethods.get(callId);
        if (clientMethod != null) {
            clientMethod.countDown();
        }
    }

    public void awaitMethod(int callId, long timeout) {
        RemoteClientMethod clientMethod = clientMethods.get(callId);
        if (clientMethod != null) {
            try {
                clientMethod.await(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
                clientMethod.callback.onInterrupt(null);
            }
        }
    }

    public boolean isMethodCountDown(int callId) {
        RemoteClientMethod clientMethod = clientMethods.get(callId);
        if (clientMethod != null) {
            return clientMethod.isCountDown();
        }
        return false;
    }

    public void removeMethod(int callId) {
        clientMethods.remove(callId);
    }

    public void printCache() {
        System.out.println(clientMethods);
    }

    public void addMethod(int callId, RemoteClientMethod clientMethod) {
        clientMethods.put(callId, clientMethod);
    }
}
