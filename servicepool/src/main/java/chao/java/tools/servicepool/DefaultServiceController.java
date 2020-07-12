package chao.java.tools.servicepool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import chao.android.tools.interceptor.Interceptor;
import chao.android.tools.interceptor.OnInvoke;
import chao.java.tools.servicepool.combine.CombineManager;
import chao.java.tools.servicepool.combine.CombineStrategy;
import chao.java.tools.servicepool.debug.Debug;

/**
 * @author qinchao
 * @since 2019/5/3
 */
public class DefaultServiceController implements ServiceController {

    private Map<String, ServiceProxy> serviceCache = new ConcurrentHashMap<>();

    private Map<String, ServiceProxy> historyCache = new ConcurrentHashMap<>(); //todo 没有考虑多classloader的场景

    private NoOpInstanceFactory noOpFactory;

    private List<IServiceFactories> factoriesList = new ArrayList<>(1);

    private CombineManager combineManager;

    private final Object serviceLock = new Object();


    private ServiceInterceptorStrategy strategy;

    private ExceptionHandler exceptionHandler;

    public DefaultServiceController() {
        noOpFactory = new NoOpInstanceFactory();

        combineManager = new CombineManager();

        strategy = new ServiceInterceptorStrategy();
    }

    @Override
    public void addService(Class<? extends IService> serviceClass) {

        ServiceProxy proxy = serviceCache.get(serviceClass.getName());
        if (proxy == null) {
            proxy = new ServiceProxy(serviceClass);
        }

        cacheService(serviceClass, proxy);

//        cacheSubClasses(serviceClass, proxy);

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

    public <T extends IService> T getCombineService(Class<T> serviceClass) {
        return combineManager.getCombineService(serviceClass, factoriesList);
    }

    public <T extends IService> T getCombineService(Class<T> serviceClass, CombineStrategy strategy) {
        return combineManager.getCombineService(serviceClass, factoriesList, strategy);
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
    public <T> T getServiceByClass(Class<T> t) {
        T instance = null;
        ServiceProxy serviceProxy = getService(t);
        if (serviceProxy != null) {
            instance = t.cast(serviceProxy.getService());
        }
        if (instance == null) {
            return noOpFactory.newInstance(t);
        }
        return getServiceByClassInternal(t, instance);
    }


    public <T extends IService> T getServiceByClass(Class<T> t, T defaultService) {
        ServiceProxy serviceProxy = getService(t);
        T instance = null;
        if (serviceProxy != null) {
            instance = t.cast(serviceProxy.getService());
        }
        if (instance == null) {
            return defaultService;
        }
        return getServiceByClassInternal(t, instance);
    }

    private <T> T getServiceByClassInternal(Class<T> t, T instance) {
        if (t.isInterface()) {
            final T finalInstance = instance;
            return Interceptor.of(instance, t).intercepted(true).invoke(new OnInvoke<T>() {

                class ResultHolder {
                    Object result;
                }
                @Override
                public Object onInvoke(T source, final Method method, final Object[] args) {
                    final ResultHolder holder = new ResultHolder();
                    getCombineService(IServiceInterceptor.class, strategy).intercept(finalInstance, method, args, new IServiceInterceptorCallback() {
                        @Override
                        public void onContinue(Method interceptorMethod, Object... interceptorArgs) {
                            try {
                                holder.result = method.invoke(finalInstance, args);
                            } catch (Throwable e) {
                                if (exceptionHandler != null) {
                                    exceptionHandler.onException(e, e.getMessage());
                                }
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onInterrupt(Object lastResult) {
                            holder.result = lastResult;
                        }
                    });
                    return holder.result;
                }

            }).newInstance();
        } else {
            return instance;
        }
    }

    public void addFactories(IServiceFactories factories) {
        factoriesList.add(factories);
    }

    @Override
    public ServiceProxy getProxy(Class<?> clazz) {
        return getService(clazz);
    }

    @Override
    public void recycleService(Class clazz) {
        historyCache.remove(clazz.getName());
        serviceCache.remove(clazz.getName());
    }

    public void cacheService(IService service) {
        ServiceProxy proxy = new InnerProxy<>(service);
        historyCache.put(service.getClass().getName(), proxy);
    }

    public Class<? extends IService> getServiceByPath(String path) {
        IPathService pathServices = getPathService();
        return pathServices.get(path);
    }

    public IPathService getPathService() {
        return getServiceByClass(IPathService.class);
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        this.combineManager.setExceptionHandler(exceptionHandler);
    }
}
