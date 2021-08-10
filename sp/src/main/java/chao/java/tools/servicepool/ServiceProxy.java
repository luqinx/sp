package chao.java.tools.servicepool;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import chao.java.tools.servicepool.annotation.Init;
import chao.java.tools.servicepool.annotation.Service;
import chao.java.tools.servicepool.cache.AbsServiceCacheStrategy;
import chao.java.tools.servicepool.cache.Soft;
import chao.java.tools.servicepool.cache.custom.Custom;
import chao.java.tools.servicepool.cache.Global;
import chao.java.tools.servicepool.cache.Once;
import chao.java.tools.servicepool.cache.Weak;
import chao.java.tools.servicepool.cache.custom.CustomCacheStrategy;

/**
 * Service的代理
 * 创建Service对象
 * Service缓存策略
 * <p>
 * 如果Service是一个非静态内部类, 则缓存策略不可以使用Global, 因为使用Global可以导致外部类不被回收而导致内存泄露
 *
 * @author qinchao
 * @since 2019/5/5
 */
public class ServiceProxy<T extends IService> {

    /**
     *  最终查找到的实现类Service class
     */
    private Class<T> serviceClass;

    private AbsServiceCacheStrategy<T> strategy;

    private int priority;

    private int scope;

    private boolean async;

    private List<Class<? extends IInitService>> dependencies;

    private IServiceFactory serviceFactory;

    /**
     *  原始class
     */
    private Class<T> originClass;

    private boolean disableIntercept;

    ServiceProxy(Class<T> clazz) {
        serviceClass = clazz;
        priority = Sp.NORMAL_PRIORITY;
        scope = Sp.SCOPE_GLOBAL;

        Service service = serviceClass.getAnnotation(Service.class);
        if (service != null) {
            priority = service.priority();
            scope = service.scope();
        }
        Init init = serviceClass.getAnnotation(Init.class);
        if (init != null) {
            async = init.async();
            dependencies = Arrays.asList(init.dependencies());
        }
    }

    public ServiceProxy(Class<T> clazz, IServiceFactory serviceFactory,
                        int priority, int scope, boolean disableIntercept, boolean async, List<Class<? extends IInitService>> dependencies) {
        this.serviceClass = clazz;
        this.serviceFactory = serviceFactory;
        this.priority = priority;
        this.scope = scope;
        this.async = async;
        this.dependencies = dependencies;
        this.disableIntercept = disableIntercept;

//        int modifiers = serviceClass.getModifiers();
//        //非静态内部类不允许使用global缓存策略
//        if ((modifiers & Modifier.STATIC) == 0
//                && serviceClass.getSimpleName().contains("$")
//                && this.scope == IService.Scope.global) {
//            System.err.println("global scope should not used on inner class.");
////            this.scope = IService.Scope.temp;
//        }

    }

    public T getService() {
        if (strategy == null) {
            synchronized (this) {
                if (strategy == null) {
                    switch (scope()) {
                        case Sp.SCOPE_GLOBAL:
                            strategy = new Global<>(serviceFactory);
                            break;
                        case Sp.SCOPE_ONCE:
                            strategy = new Once<>(serviceFactory);
                            break;
                        case Sp.SCOPE_WEAK:
                            strategy = new Weak<>(serviceFactory);
                            break;
                        case Sp.SCOPE_SOFT:
                            strategy = new Soft<>(serviceFactory);
                            break;
                        default:
                            if ((scope() & Sp.SCOPE_MASK) != 0) {
                                throw new ServicePoolException("scope with mask 0xf0000000 is reserved for scopes.");
                            }
                            strategy = new Custom<>(scope(), serviceFactory);
                            break;
                    }
                }
            }
        }
        strategy.setDisableIntercept(disableIntercept);
        T service = strategy.getService(serviceClass, originClass);
        tryInitService(service);
        return service;
    }

    private void tryInitService(IService service) {
        if (service instanceof IInitService) {
            DependencyManager dependencyManager = ServicePool.getService(DependencyManager.class);
            dependencyManager.tryInitService((IInitService) service, dependencies, async);
        }
    }

    Class<? extends IService> getServiceClass() {
        return serviceClass;
    }

    /**
     *
     */
    public int priority() {
        return priority;
    }

    /**
     *
     */
    public int scope() {
        if (IInitService.class.isAssignableFrom(serviceClass)
                || IPathService.class.isAssignableFrom(serviceClass)
            || CustomCacheStrategy.class.isAssignableFrom(serviceClass)) {
            return Sp.SCOPE_GLOBAL;
        }
        return scope;
    }

    public int realScope() {
        return scope;
    }

    public List<Class<? extends IInitService>> dependencies() {
        return dependencies;
    }

    public boolean async() {
        return async;
    }

    public void setDisableIntercept(boolean disableIntercept) {
        this.disableIntercept = disableIntercept;
        if (strategy != null) {
            strategy.setDisableIntercept(disableIntercept);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceProxy proxy = (ServiceProxy) o;

        return Objects.equals(serviceClass, proxy.serviceClass);
    }

    @Override
    public int hashCode() {
        return serviceClass != null ? serviceClass.hashCode() : 0;
    }


    public void setOriginClass(Class<? extends IService> originClass) {
        this.originClass = (Class<T>) originClass;
    }

    public Class<T> getOriginClass() {
        return originClass;
    }
}
