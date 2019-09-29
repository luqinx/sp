package chao.java.tools.servicepool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinchao
 * @since 2019/5/3
 */
public class DefaultServiceController implements ServiceController {

    private Map<String, ServiceProxy> serviceCache = new HashMap<>();

    private Map<String, ServiceProxy> historyCache = new HashMap<>();

    private NoOpInstanceFactory noOpFactory;

    private List<IServiceFactories> factoriesList = new ArrayList<>(1);


    public DefaultServiceController() {
        noOpFactory = new NoOpInstanceFactory();
    }

    @Override
    public void addService(Class<? extends IService> serviceClass) {

        ServiceProxy proxy = serviceCache.get(serviceClass.getName());
        if (proxy == null) {
            proxy = new ServiceProxy(serviceClass);
        }

        cacheService(serviceClass, proxy);

        cacheSubClasses(serviceClass, proxy);

    }

    private void cacheService(Class<?> serviceClass, ServiceProxy proxy) {
        if (serviceClass == Object.class) {
            return;
        }
        ServiceProxy oldProxy = serviceCache.get(serviceClass.getName());
        //1. service还不存在
        //2. 申请的serviceClass和缓存key一致时，属于第一优先级
        //3. service已存在，但是当前的service优先级更高
        if (oldProxy == null || (!oldProxy.getServiceClass().equals(serviceClass)
            && (proxy.priority() > oldProxy.priority()) || proxy.getServiceClass().equals(serviceClass))) {
            serviceCache.put(serviceClass.getName(), proxy);
        }
    }

    private void cacheSubClasses(Class<?> clazz, ServiceProxy serviceProxy) {
        if (clazz == Object.class) {
            return;
        }
        for (Class<?> subInterface : clazz.getInterfaces()) {
            if (IService.class.equals(subInterface)) {
                continue;
            }
            if (IInitService.class.equals(subInterface)) {
                continue;
            }
            cacheService(subInterface, serviceProxy);
            cacheSubClasses(subInterface, serviceProxy);
        }
        Class superClass = clazz.getSuperclass();
        if (superClass == Object.class) {
            return;
        }
        if (superClass != null) {

            cacheService(superClass, serviceProxy);
            cacheSubClasses(superClass, serviceProxy);
        }
    }

    private ServiceProxy getService(Class<?> serviceClass) {

        long getServiceStart = System.currentTimeMillis();

        ServiceProxy record = historyCache.get(serviceClass.getName());
        if (record != null) {
            return record;
        }

        ServiceProxy cachedProxy = serviceCache.get(serviceClass.getName());
        //申请的Service和缓存的Service同类型，属于最高优先级，直接返回
        if (cachedProxy != null && (cachedProxy.getServiceClass() == serviceClass)) {
            return cachedProxy;
        }
        ServiceProxy proxy = null;
        //目前只有一个ServiceFactories
        for (IServiceFactories factories: factoriesList) {
            String name = serviceClass.getName();
            int last = name.lastIndexOf('.');
            if (last == -1) {
                continue;
            }
            String pkgName = name.substring(0, last);
            IServiceFactory factory = factories.getServiceFactory(pkgName);
            if (factory == null) {
                continue;
            }
            proxy = factory.createServiceProxy(serviceClass);
            if (proxy != null) {
                cacheService(proxy.getServiceClass(), proxy);
                addService(proxy.getServiceClass());
                proxy = serviceCache.get(proxy.getServiceClass().getName());
            }
        }
        if (proxy == null) {
            proxy = cachedProxy;
        }
        long getServiceEnd = System.currentTimeMillis();
        if (proxy != null) {
            historyCache.put(serviceClass.getName(), proxy);
            System.out.println("get service " + proxy.getServiceClass() + " spent:" + (getServiceEnd - getServiceStart));
        }
        return proxy;
    }


    public void addServices(Iterable<Class<? extends IService>> services) {
        for (Class<? extends IService> serviceClass: services) {
            addService(serviceClass);
        }
    }

    @Override
    public <T> T getServiceByClass(Class<T> t) {
        ServiceProxy serviceProxy = getService(t);
        if (serviceProxy != null) {
            return t.cast(serviceProxy.getService());
        }
        return noOpFactory.newInstance(t);
    }

    @Override
    public void loadFinished() {
    }

    public <T extends IService> T getServiceByClass(Class<T> tClass, T defaultService) {
        ServiceProxy serviceProxy = getService(tClass);
        if (serviceProxy != null) {
            return tClass.cast(serviceProxy.getService());
        }
        return defaultService;
    }

    public void addFactories(IServiceFactories factories) {
        factoriesList.add(factories);
    }

    @Override
    public ServiceProxy getProxy(Class<?> clazz) {
        return getService(clazz);
    }

    public void cacheService(IService service) {
        ServiceProxy proxy = new InnerProxy<>(service);
        historyCache.put(service.getClass().getName(), proxy);
    }

    public Class<? extends IService> getPathService(String path) {
        IPathService pathServices = getServiceByClass(IPathService.class);
        return pathServices.get(path);
    }
}
