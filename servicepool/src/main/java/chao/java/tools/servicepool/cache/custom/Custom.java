package chao.java.tools.servicepool.cache.custom;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactory;
import chao.java.tools.servicepool.SP;
import chao.java.tools.servicepool.cache.AbsServiceCacheStrategy;

/**
 * @author luqin
 * @since 2020-08-06
 */
public final class Custom<T extends IService> extends AbsServiceCacheStrategy<T> {

    private final int customScope;

    private IServiceFactory factory;

    public Custom(int customScope, IServiceFactory factory) {
        this.factory = factory;
        this.customScope = customScope;
    }

    /**
     *
     * @param serviceClass  经过 {@link chao.java.tools.servicepool.ServicePool} 查找后的class
     *
     * @param originClass   对应 {@link chao.java.tools.servicepool.ServicePool#getService(Class)}方法的参数class
     *
     */
    @SuppressWarnings("unchecked")
    @Override
    public T getService(Class<T> serviceClass, Class<T> originClass) {
        CustomCacheStrategy<T> customCache =  SP.getCombineService(CustomCacheStrategy.class, new CustomCombineStrategy(customScope));
        return getProxyService(originClass, customCache.getService(factory, originClass, serviceClass));
    }
}
