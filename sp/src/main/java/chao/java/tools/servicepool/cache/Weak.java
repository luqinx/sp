package chao.java.tools.servicepool.cache;

import java.lang.ref.WeakReference;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactory;
import chao.java.tools.servicepool.ReflectUtil;

/**
 * 使用弱引用
 *
 * 如果不被gc回收，则不会重新创建
 */
public final class Weak<T extends IService> extends AbsServiceCacheStrategy<T> {

    private IServiceFactory factory;

    private WeakReference<T> weakService;

    public Weak(IServiceFactory factory) {
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
        return getProxyService(originClass, weakService.get());
    }
}