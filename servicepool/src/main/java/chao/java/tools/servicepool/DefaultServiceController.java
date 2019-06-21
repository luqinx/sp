package chao.java.tools.servicepool;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author qinchao
 * @since 2019/5/3
 */
public class DefaultServiceController implements ServiceController {

    private Map<Integer, ServiceProxy> serviceCache = new HashMap<>();

    private DependencyManager dependencyManager;

    private NoOpInstanceFactory noOpFactory;


    public DefaultServiceController() {
        noOpFactory = new NoOpInstanceFactory();
        ServiceLoader<DependencyManager> dependencyManagers = ServiceLoader.load(DependencyManager.class);
        for (DependencyManager dependencyManager: dependencyManagers) {
            this.dependencyManager = dependencyManager;
        }
        if (dependencyManager == null) {
            this.dependencyManager = new DefaultDependencyManager();
        }
    }

    @Override
    public void addService(Class<? extends IService> serviceClass) {

        ServiceProxy proxy = serviceCache.get(serviceClass.hashCode());
        if (proxy == null) {
            proxy = new ServiceProxy(serviceClass);
        }

        IService service = proxy.getService();

        if (!serviceCache.containsKey(serviceClass.hashCode())) {
            serviceCache.put(serviceClass.hashCode(), proxy);
        }
        cacheSubClasses(serviceClass, proxy);
        if (service != null) {
            cacheSubClasses(service.getClass(), proxy);
        }

        if (service instanceof IInitService) {
            IInitService initService = (IInitService) service;
            dependencyManager.addService(initService);
        }
    }

    private void cacheSubClasses(Class<?> clazz, ServiceProxy serviceProxy) {
        for (Class<?> subInterface: clazz.getInterfaces()) {
            if (IService.class.equals(subInterface)) {
                continue;
            }
            if (IInitService.class.equals(subInterface)) {
                continue;
            }
            if (!serviceCache.containsKey(subInterface.hashCode())) {
                serviceCache.put(subInterface.hashCode(), serviceProxy);
            }
        }
        Class superClass = clazz;
        while (superClass != null) {
            if (!serviceCache.containsKey(superClass.hashCode())) {
                serviceCache.put(superClass.hashCode(), serviceProxy);
            }
            superClass = superClass.getSuperclass();
        }
    }

    private ServiceProxy getService(Class<?> serviceClass) {
        return serviceCache.get(serviceClass.hashCode());
    }


    public void addServices(Iterable<Class<? extends IService>> services) {
        for (Class<? extends IService> serviceClass: services) {
            addService(serviceClass);
        }
    }

    @Override
    public <T extends IService> T getServiceByClass(Class clazz, Class<T> t) {
        ServiceProxy serviceProxy = getService(clazz);
        if (serviceProxy != null) {
            return t.cast(serviceProxy.getService());
        }
        return noOpFactory.newInstance(t);
    }

    @Override
    public void loadFinished() {
        dependencyManager.servicesInit();
    }

    public <T extends IService> T getServiceByClass(Class serviceClass, Class<T> tClass, T service) {
        ServiceProxy serviceProxy = getService(serviceClass);
        if (serviceProxy != null) {
            return tClass.cast(serviceProxy.getService());
        }
        return service;
    }
}
