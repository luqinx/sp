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

    private Map<Class<? extends IService>, String> pathCache = new ConcurrentHashMap<>();

    private Map<String, ServiceProxy<? extends IService>> historyCache = new ConcurrentHashMap<>(); //todo 没有考虑多classloader的场景

    private Map<String, ServiceProxy<? extends IService>> fixedCache = new ConcurrentHashMap<>(); //todo 没有考虑多classloader的场景


    private List<IServiceFactories> factoriesList = new ArrayList<>(1);

    private CombineManager combineManager;

    private final Object serviceLock = new Object();


    public DefaultServiceController() {
        combineManager = new CombineManager();
    }

    public void cacheService(Class<?> serviceClass, ServiceProxy<? extends IService> serviceProxy) {
        if (serviceClass == Object.class || serviceClass == null) {
            return;
        }
        cacheService(serviceClass.getName(), serviceProxy);
    }

    public void cacheService(String name, ServiceProxy<? extends IService> serviceProxy) {
        historyCache.put(name, serviceProxy);
    }

    private void cacheSubServices(Class<?> clazz, ServiceProxy<? extends IService> serviceProxy) {
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
            if (!IService.class.isAssignableFrom(clazz)) {
                continue;
            }
            cacheService(subInterface.getName(), serviceProxy);
            cacheSubServices(subInterface, serviceProxy);
        }
        Class superClass = clazz.getSuperclass();
        if (superClass == Object.class || superClass == null) {
            return;
        }

        if (!IService.class.isAssignableFrom(superClass)) {
            return;
        }
        cacheService(superClass.getName(), serviceProxy);
        cacheSubServices(superClass, serviceProxy);
    }

    public <T extends IService> T getCombineService(Class<T> serviceClass) {
        return getCombineService(serviceClass,  null);
    }

    public <T extends IService> T getCombineService(Class<T> serviceClass, CombineStrategy strategy) {
        return combineManager.getCombineService(serviceClass, factoriesList, strategy);
    }

    /**
     *
     * 获取一个serviceClass的代理对象, 最后的serviceClass由这个代理通过不同策略来生成。
     *
     *
     * @param serviceClass 查找源class
     * @return ServiceProxy
     */
    private ServiceProxy<? extends IService> getService(Class<? extends IService> serviceClass) {

        ServiceProxy<? extends IService> record = historyCache.get(serviceClass.getName());
        if (record != null) {
            return record;
        }

        ServiceProxy<? extends IService> newRecord = null;
        synchronized (serviceLock) {
            //同步后再查询一次， double check
            record = historyCache.get(serviceClass.getName());
            if (record != null && record.getOriginClass() == serviceClass) {
                return record;
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
                newRecord = factory.createServiceProxy(serviceClass);
                if (newRecord == null) {
                    continue;
                }
                newRecord.setOriginClass(serviceClass);

                ServiceProxy<? extends IService> fixedRecord = fixedCache.get(serviceClass.getName());
                if (fixedRecord != null && fixedRecord.getOriginClass() == serviceClass) {
                    newRecord = fixedRecord; //如果proxy已经通过fixed创建， 直接使用fixed proxy, 避免创建重复proxy
                }

                cacheService(serviceClass.getName(), newRecord);
                cacheSubServices(serviceClass, newRecord);
                break;
            }
        }
        return newRecord;
    }

    private ServiceProxy<? extends IService> getFixedService(Class<? extends IService> serviceClass) {
        // 1. 先去fixed缓存去拿
        ServiceProxy<? extends IService> record = fixedCache.get(serviceClass.getName());
        if (record != null) {
            return record;
    }

        // 2. 尝试去history缓存去拿， 但是需要确认history缓存的originClass和serviceClass是一致的
        record = historyCache.get(serviceClass.getName());
        if (record != null && record.getOriginClass() == serviceClass) {
            fixedCache.put(serviceClass.getName(), record);
            return record;
        }

        // 3.如果history缓存没有，或者history缓存的originClass和serviceClass不一致
        ServiceProxy<? extends IService> newRecord = null;
        synchronized (serviceLock) {
            //同步后再查询一次， double check
            record = fixedCache.get(serviceClass.getName());
            if (record != null && record.getOriginClass() == serviceClass) {
                return record;
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
                newRecord = factory.createFixedServiceProxy(serviceClass);
                if (newRecord == null) {
                    continue;
                }
                newRecord.setOriginClass(serviceClass);


                fixedCache.put(serviceClass.getName(), newRecord);
                break;
            }
        }
        return newRecord;
    }


    void addServices(Iterable<Class<? extends IService>> services) {
        for (Class<? extends IService> serviceClass : services) {
            addService(serviceClass);
        }
    }

    private void addService(Class<? extends IService> serviceClass) {
        ServiceProxy<? extends IService> proxy = new ServiceProxy<>(serviceClass);
        cacheService(serviceClass, proxy);
        cacheSubServices(serviceClass, proxy);

        if (IServiceFactories.class.isAssignableFrom(serviceClass)) {
            Debug.addError("cache factories service: " + serviceClass);
            addFactories((IServiceFactories) getServiceByClass(serviceClass));
        }
    }

    @Override
    public void loadFinished() {
    }

    @Override
    public <T extends IService> T getFixedServiceByClass(Class<T> t) {
        T instance = null;
        ServiceProxy serviceProxy = getFixedService(t);
        if (serviceProxy != null) {
            instance = t.cast(serviceProxy.getService());
        }
        return instance;
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
    }


    public Class<? extends IService> getServiceByPath(String path) {
        IPathService pathService = getPathService();
        Class<? extends IService> service = pathService.get(path);
        if (service != null) {
            pathCache.put(service, path);
        }
        return service;
    }

    public IPathService getPathService() {
        return getServiceByClass(IPathService.class);
    }

}
