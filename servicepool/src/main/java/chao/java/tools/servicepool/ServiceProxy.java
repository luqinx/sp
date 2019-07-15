package chao.java.tools.servicepool;

import chao.java.tools.servicepool.annotation.Service;

/**
 * @author qinchao
 * @since 2019/5/5
 */
public class ServiceProxy {

    private Class<? extends IService> serviceClass;

    private ServiceCacheStrategy strategy;

    private int priority;

    private int scope;

    private String tag;

    private IServiceFactory serviceFactory;

    ServiceProxy(Class<? extends IService> clazz) {
        serviceClass = clazz;
        priority = IService.Priority.NORMAL_PRIORITY;
        scope = IService.Scope.global;

        Service service = serviceClass.getAnnotation(Service.class);
        if (service != null) {
            priority = service.priority();
            scope = service.scope();
            tag = service.value();
        }
    }

    public ServiceProxy(Class<? extends IService> clazz, IServiceFactory serviceFactory, int priority, int scope, String tag) {
        this.serviceClass = clazz;
        this.serviceFactory = serviceFactory;
        this.priority = priority;
        this.scope = scope;
        this.tag = tag;
    }

    IService getService() {
        if (strategy == null) {
            switch (scope()) {
                case IService.Scope.global:
                    strategy = new ServiceCacheStrategy.Global(serviceFactory);
                    break;
                case IService.Scope.once:
                    strategy = new ServiceCacheStrategy.Once(serviceFactory);
                    break;
                case IService.Scope.temp:
                    strategy = new ServiceCacheStrategy.Temp(serviceFactory);
                    break;
                default:
                    strategy = new ServiceCacheStrategy.Global(serviceFactory);
                    break;
            }
        }
        return strategy.getService(serviceClass);
    }

    Class<? extends IService> getServiceClass() {
        return serviceClass;
    }

    /**
     */
    public String tag() {
        return tag;
    }

    /**
     */
    public int priority() {
        return priority;
    }

    /**
     */
    public int scope() {
        return scope;
    }
}
