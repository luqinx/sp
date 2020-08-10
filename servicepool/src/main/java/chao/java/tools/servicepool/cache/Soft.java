package chao.java.tools.servicepool.cache;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactory;
import chao.java.tools.servicepool.ReflectUtil;

/**
 * 使用软引用
 *
 * 如果不被gc回收，则不会重新创建
 */
public final class Soft<T extends IService> extends AbsServiceCacheStrategy<T> {

    private IServiceFactory factory;

    private SoftReference<T> weakService;

    public Soft(IServiceFactory factory) {
        this.factory = factory;
    }

    @Override
    public T getService(Class<T> serviceClass, Class<T> originClass) {
        if (weakService == null || weakService.get() == null) {
            synchronized (this) {
                if (weakService == null || weakService.get() == null) {

                    if (factory != null) {
                        weakService = new SoftReference<>(serviceClass.cast(factory.createInstance(serviceClass)));
                    } else {
                        weakService = new SoftReference<>(ReflectUtil.newInstance(serviceClass));
                    }
                }
            }
        }
        return getProxyService(originClass, weakService.get());
    }
}