package chao.java.tools.servicepool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import chao.java.tools.servicepool.combine.CombineManager;
import chao.java.tools.servicepool.combine.CombineStrategy;
import chao.java.tools.servicepool.debug.Debug;

/**
 * @author qinchao
 * @since 2019/5/3
 */
public class DefaultServiceController implements ServiceController {

    private Map<String, ServiceProxy<? extends IService>> serviceCache = new ConcurrentHashMap<>();

    private Map<String, ServiceProxy<? extends IService>> historyCache = new ConcurrentHashMap<>(); //todo 没有考虑多classloader的场景


    private List<IServiceFactories> factoriesList = new ArrayList<>(1);

    private CombineManager combineManager;

    private final Object serviceLock = new Object();


    public DefaultServiceController() {
        combineManager = new CombineManager();
    }

    @Override
    public void addService(Class<? extends IService> serviceClass) {

        ServiceProxy proxy = serviceCache.get(serviceClass.getName());
        if (proxy == null) {
            proxy = new ServiceProxy<>(serviceClass);
        }

        cacheService(serviceClass, proxy);

//        cacheSubClasses(serviceClass, proxy);

    }

    private void cacheService(Class<?> serviceClass, ServiceProxy<? extends IService> proxy) {
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

    private void cacheSubClasses(Class<?> clazz, ServiceProxy<? extends IService> serviceProxy) {
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

    public <T extends IService> T getCombineService(Class<T> serviceClass) {
        return combineManager.getCombineService(serviceClass, factoriesList);
    }

    public <T extends IService> T getCombineService(Class<T> serviceClass, CombineStrategy strategy) {
        return combineManager.getCombineService(serviceClass, factoriesList, strategy);
    }


    private ServiceProxy<? extends IService> getService(Class<? extends IService> serviceClass) {

        long getServiceStart = System.currentTimeMillis();

        ServiceProxy<? extends IService> record = historyCache.get(serviceClass.getName());
        if (record != null) {
            return record;
        }

        ServiceProxy<? extends IService> cachedProxy = serviceCache.get(serviceClass.getName());
        //申请的Service和缓存的Service同类型，属于最高优先级，直接返回
        if (cachedProxy != null && (cachedProxy.getServiceClass() == serviceClass)) {
            return cachedProxy;
        }
        ServiceProxy<? extends IService> proxy = null;
        synchronized (serviceLock) {
            record = historyCache.get(serviceClass.getName());
            if (record != null) {
                return record;
            }

            cachedProxy = serviceCache.get(serviceClass.getName());
            //申请的Service和缓存的Service同类型，属于最高优先级，直接返回
            if (cachedProxy != null && (cachedProxy.getServiceClass() == serviceClass)) {
                return cachedProxy;
            }

            //目前只有一个ServiceFactories
            for (IServiceFactories factories : factoriesList) {
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
                    proxy.setOriginClass(serviceClass);
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
                System.out.println("get service " + serviceClass.getName() + " spent:" + (getServiceEnd - getServiceStart));
            }
        }
        return proxy;
    }


    public void addServices(Iterable<Class<? extends IService>> services) {
        for (Class<? extends IService> serviceClass: services) {
            Debug.addError("cache factories service: " + serviceClass);
            addService(serviceClass);
            if (IServiceFactories.class.isAssignableFrom(serviceClass)) {
                addFactories((IServiceFactories) getServiceByClass(serviceClass));
            }
        }
    }

    @Override
    public void loadFinished() {
    }

    @Override
    public <T extends IService> T getServiceByClass(Class<T> t) {
        T instance = null;
        ServiceProxy serviceProxy = getService(t);
        if (serviceProxy != null) {
            instance = t.cast(serviceProxy.getService());
        }
        return instance;
    }


    public <T extends IService> T getServiceByClass(Class<T> t, T defaultService) {
        ServiceProxy<? extends IService> serviceProxy = getService(t);
        T instance = null;
        if (serviceProxy != null) {
            instance = t.cast(serviceProxy.getService());
        }
        if (instance == null) {
            return defaultService;
        }
        return instance;
    }

    public void addFactories(IServiceFactories factories) {
        factoriesList.add(factories);
    }

    @Override
    public ServiceProxy<? extends IService> getProxy(Class<? extends IService> clazz) {
        return getService(clazz);
    }

    @Override
    public void recycleService(Class clazz) {
        historyCache.remove(clazz.getName());
        serviceCache.remove(clazz.getName());
    }

    public void cacheService(IService service) {
        ServiceProxy<? extends IService> proxy = new InnerProxy<>(service);
        historyCache.put(service.getClass().getName(), proxy);
    }

    public Class<? extends IService> getServiceByPath(String path) {
        IPathService pathServices = getPathService();
        return pathServices.get(path);
    }

    public IPathService getPathService() {
        return getServiceByClass(IPathService.class);
    }

}
