package chao.java.tools.servicepool.cache;

import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2020-07-23
 */
public class Specific<T extends IService> extends AbsServiceCacheStrategy<T> {

    private T service;

    public Specific(T service) {
        this.service = service;
    }

    @Override
    public T getService(Class<T> serviceClass, Class<T> originClass) {
        return getProxyService(originClass, service);
    }
}
