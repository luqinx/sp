package chao.java.tools.servicepool;

import java.util.Arrays;
import java.util.List;

import chao.java.tools.servicepool.annotation.Init;
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

    private boolean async;

    private List<Class<? extends IInitService>> dependencies;

    private IServiceFactory serviceFactory;

    ServiceProxy(Class<? extends IService> clazz) {
        serviceClass = clazz;
        priority = IService.Priority.NORMAL_PRIORITY;
        scope = IService.Scope.global;

        Service service = serviceClass.getAnnotation(Service.class);
        if (service != null) {
            priority = service.priority();
            scope = service.scope();
            tag = service.tag();
        }
        Init init = serviceClass.getAnnotation(Init.class);
        if (init != null) {
            async = init.async();
            dependencies = Arrays.asList(init.dependencies());
        }
    }

    public ServiceProxy(Class<? extends IService> clazz, IServiceFactory serviceFactory, int priority, int scope, String tag) {
        this.serviceClass = clazz;
        this.serviceFactory = serviceFactory;
        this.priority = priority;
        this.scope = scope;
        this.tag = tag;

        //todo for dev debug
        Init init = serviceClass.getAnnotation(Init.class);
        if (init != null) {
            async = init.async();
            dependencies = Arrays.asList(init.dependencies());
        }
    }

    public IService getService() {
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
        IService service = strategy.getService(serviceClass);
        if (service instanceof IInitService) {
            DependencyManager dependencyManager = ServicePool.getService(DependencyManager.class);
            dependencyManager.tryInitService((IInitService) service, dependencies, async);
        }
        return service;
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

    public List<Class<? extends IInitService>> dependencies() {
        return dependencies;
    }

    public boolean async() {
        return async;
    }
}
