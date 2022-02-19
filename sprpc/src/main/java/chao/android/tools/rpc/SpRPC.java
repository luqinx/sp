package chao.android.tools.rpc;

import android.content.Context;
import chao.android.tools.servicepool.Spa;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.InnerProxy;
import chao.java.tools.servicepool.ServicePool;
import com.luqinx.interceptor.Interceptor;

/**
 * @author luqin
 * @since 2020-07-27
 */
public class SpRPC {

    /**
     *  初始化
     */
    public static void init(Context context) {
        Spa.init(context);
    }

    /**
     * 获取远程(其他进程)service服务， 实现RPC通信
     *
     * 默认不允许在主线程调用remoteService方法
     *
     *
     * todo:
     * 1. 参数序列化 (基本类型， map, bundle...)
     * 2. 异步拦截器  (必须异步执行， 避免阻塞主线程)      done.
     * 3. client端， 注解配置 (timeout, serviceName, forceMainThread) done
     * 4. 校验c/s两端通信安全，并兼顾通信效率       done.
     *
     */
    public static <T extends IService> T getService(Class<T> serviceClass) {
        if (!serviceClass.isInterface()) {
            throw new RemoteServiceException("serviceClass must be a interface class.");
        }

        //1. 如果本地服务存在， 优先取本地服务
        //2. 如果缓存存在直接取缓存
        T remoteService = ServicePool.getService(serviceClass, null);
        if (remoteService == null) {
            T service = Interceptor.of(null, serviceClass).interfaces(RemoteService.class).intercepted(true).newInstance();

            InnerProxy<T> innerProxy = new InnerProxy<>(service);
            innerProxy.setOriginClass(serviceClass);

            ServicePool.cacheService(serviceClass, innerProxy);
            remoteService = innerProxy.getService();
        }

        return remoteService;
    }

    /**
     * 检查远程服务是否存在
     *
     *
     * @return 如果远程app没有安装，返回false
     */
    public static <T extends IService> boolean remoteServiceExist(Class<T> serviceClass) {
        IService service = getService(serviceClass);
        if (service instanceof RemoteService) {
            RemoteService remoteService = (RemoteService) service;
            return remoteService.remoteExist();
        }
        return false;
    }
}
