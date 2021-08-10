package chao.java.tools.servicepool.cache;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactory;
import chao.java.tools.servicepool.ReflectUtil;

/**
 * 全局策略， 类似一个单例
 */
public final class Global<T extends IService> extends AbsServiceCacheStrategy<T> {

    private T service;

    private IServiceFactory factory;

    public Global(IServiceFactory factory) {
        this.factory = factory;
    }

    @Override
    public T getService(Class<T> serviceClass, Class<T> originClass) {
        if (service == null) {
            synchronized (this) {
                if (service == null) {
                    if (factory != null) {
                        service = serviceClass.cast(factory.createInstance(serviceClass));
                    } else {
                        service = ReflectUtil.newInstance(serviceClass);
                    }
                    service = getProxyService(originClass, service);
                }
            }
        }
        return service;
    }
}