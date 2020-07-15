package chao.java.tools.servicepool;

/**
 * @author luqin
 * @since 2019-09-03
 */
class InnerProxy<T extends IService> extends ServiceProxy<T> {

    private T service;

    InnerProxy(T service) {
        super((Class<T>) service.getClass());
        this.service = service;
    }

    @Override
    public T getService() {
        return service;
    }
}
