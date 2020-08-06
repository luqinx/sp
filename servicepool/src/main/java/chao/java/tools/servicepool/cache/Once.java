package chao.java.tools.servicepool.cache;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactory;
import chao.java.tools.servicepool.ReflectUtil;

/**
 * 每次获取时创建一个新的对象
 */
public final class Once<T extends IService> extends AbsServiceCacheStrategy<T> {

    private final IServiceFactory factory;

    public Once(IServiceFactory factory) {
        this.factory = factory;
    }

    @Override
    public T getService(Class<T> serviceClass, Class<T> originClass) {
        IService instance;
        if (factory != null) {
            instance = factory.createInstance(serviceClass);
        } else {
            instance = ReflectUtil.newInstance(serviceClass);
        }
        if (instance != null) {
            return getProxyService(originClass, serviceClass.cast(instance));
        }
        return null;
    }
}