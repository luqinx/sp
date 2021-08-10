package chao.java.tools.servicepool;

import chao.java.tools.servicepool.cache.Specific;

/**
 * @author luqin
 * @since 2019-09-03
 */
public class InnerProxy<T extends IService> extends ServiceProxy<T> {

    private T service;

    private Specific<T> cacheStrategy;

    public InnerProxy(T service) {
        super((Class<T>) service.getClass());
        this.service = service;
        cacheStrategy = new Specific<>(this.service);
        setOriginClass(service.getClass());
    }

    @Override
    public T getService() {
        return cacheStrategy.getService(getOriginClass(), getOriginClass());
    }
}
