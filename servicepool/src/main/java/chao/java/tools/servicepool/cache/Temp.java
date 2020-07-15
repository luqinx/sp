package chao.java.tools.servicepool.cache;

import java.lang.ref.WeakReference;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactory;
import chao.java.tools.servicepool.ReflectUtil;

/**
 * 临时策略， 如果不被gc回收，则不会重新创建
 */
@Deprecated
public class Temp<T extends IService> implements ServiceCacheStrategy<T> {

    private IServiceFactory factory;

    private WeakReference<T> weakService;

    public Temp(IServiceFactory factory) {
        this.factory = factory;
    }

    @Override
    public T getService(Class<T> serviceClass, Class<T> originClass) {
        if (weakService == null || weakService.get() == null) {
            synchronized (this) {
                if (weakService == null || weakService.get() == null) {

                    if (factory != null) {
                        weakService = new WeakReference<>(serviceClass.cast(factory.createInstance(serviceClass)));
                    } else {
                        weakService = new WeakReference<>(ReflectUtil.newInstance(serviceClass));
                    }
                }
            }
        }
        return weakService.get();
    }
}