package chao.java.tools.servicepool;

import chao.java.tools.servicepool.annotation.Service;

/**
 * @author qinchao
 * @since 2019/5/5
 */
public class ServiceProxy implements IService {

    private Class<? extends IService> serviceClass;

    private ServiceCacheStrategy strategy;

    private int priority;

    private int scope;

    private String tag;

    ServiceProxy(Class<? extends IService> clazz) {
        serviceClass = clazz;
        priority = Priority.NORMAL_PRIORITY;
        scope = Scope.global;

        Service service = serviceClass.getAnnotation(Service.class);
        if (service != null) {
            priority = service.priority();
            scope = service.scope();
            tag = service.value();
        }
    }

    IService getService() {
        if (strategy == null) {
            switch (scope()) {
                case Scope.global:
                    strategy = new ServiceCacheStrategy.Global();
                    break;
                case Scope.once:
                    strategy = new ServiceCacheStrategy.Once();
                    break;
                case Scope.temp:
                    strategy = new ServiceCacheStrategy.Temp();
                    break;
                default:
                    strategy = new ServiceCacheStrategy.Global();
                    break;
            }
        }
        return strategy.getService(serviceClass);
    }

    Class<?> getServiceClass() {
        return serviceClass;
    }

    /**
     * todo
     * @return
     */
    @Override
    public String getTag() {
        return tag;
    }

    /**
     * todo
     * @return
     */
    @Override
    public int priority() {
        return priority;
    }

    /**
     * todo
     *
     * @return
     */
    @Override
    public int scope() {
        return scope;
    }
}
