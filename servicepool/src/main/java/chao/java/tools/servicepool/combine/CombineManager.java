package chao.java.tools.servicepool.combine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import chao.android.tools.interceptor.Interceptor;
import chao.android.tools.interceptor.OnInvoke;
import chao.java.tools.servicepool.ExceptionHandler;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactories;
import chao.java.tools.servicepool.IServiceFactory;
import chao.java.tools.servicepool.Logger;
import chao.java.tools.servicepool.NoOpInstance;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.ServiceProxy;
import chao.java.tools.servicepool.thirdparty.CancelableCountDownLatch;


/**
 * @author luqin
 * @since 2019-09-29
 */
public class CombineManager {


    private static final String COMBINE_METHOD_GET = "get";

    private static final String COMBINE_METHOD_SIZE = "add";

    private static final String COMBINE_METHOD_ITERATOR = "iterator";

    private static final String COMBINE_METHOD_TOSTRING = "toString";


    private Logger logger = new Logger();

    private HashMap<Class, List<ServiceProxy>> combinedCache = new HashMap<>();

    private ExceptionHandler exceptionHandler;

    private DefaultCombineStrategy defaultCombineStrategy = new DefaultCombineStrategy();

    public CombineManager() {

    }

    public <T extends IService> T getCombineService(final Class<T> serviceClass, final List<IServiceFactories> factories) {
        return getCombineService(serviceClass, factories, null);
    }

    public <T extends IService> T getCombineService(final Class<T> serviceClass, final List<IServiceFactories> factories, final CombineStrategy _strategy) {
        if (serviceClass == null) {
            throw new IllegalArgumentException("argument 'serviceClass' should not be null.");
        }
        if (!serviceClass.isInterface()) {
            throw new IllegalArgumentException("argument 'serviceClass' should be a interface class.");
        }
        return Interceptor.of(null, serviceClass)
                .intercepted(true)
                .interfaces(CombineService.class, Iterable.class)
                .invoke(new OnInvoke<T>() {

                    @Override
                    public Object onInvoke(T source, final Method method, final Object[] args) {

                        List<ServiceProxy> proxies = _getCombinedServices(serviceClass, factories);

                        if (COMBINE_METHOD_SIZE.equals(method.getName()) && (args == null || args.length == 0)) {
                            //Iterable.size()
                            return proxies.size();
                        } else if (COMBINE_METHOD_GET.equals(method.getName()) && args != null && args.length == 1 && args[0] instanceof Integer) {
                            //Iterable.get(int index)
                            return proxies.get((Integer) args[0]);
                        } else if (COMBINE_METHOD_ITERATOR.equals(method.getName()) && (args == null || args.length == 0)) {
                            //Iterable.iterator()
                            List<IService> services = new ArrayList<>();
                            for (ServiceProxy proxy: proxies) {
                                services.add(proxy.getService());
                            }
                            return services.iterator();
                        } else if (COMBINE_METHOD_TOSTRING.equals(method.getName()) && (args == null || args.length == 0)) {
                            //Object.toString()
                            return String.valueOf(source);
                        }

                        if (proxies.size() == 0) {
                            return null;
                        }

                        List<CombineStrategy> services;
                        if (_strategy == null) {
                            services = new ArrayList<>();
                            Iterable<CombineStrategy> iterable = (Iterable<CombineStrategy>) ServicePool.getCombineService(CombineStrategy.class);
                            for (CombineStrategy combineStrategy: iterable) {
                                services.add(combineStrategy);
                            }
                        } else {
                            services = Collections.singletonList(_strategy);
                        }

                        for (CombineStrategy strategy: services) {
                            if(strategy != null
                                    && !(strategy instanceof NoOpInstance)
                                    && strategy.filter(serviceClass, method, args)) {
                                return strategy.invoke(proxies, serviceClass, method, args);
                            }
                        }
                        return defaultCombineStrategy.invoke(proxies, serviceClass, method, args);
                    }
                }).newInstance();
    }

    private List<ServiceProxy> _getCombinedServices(Class<?> serviceClass, List<IServiceFactories> factoriesList) {
        List<ServiceProxy> proxies = combinedCache.get(serviceClass);
        if (proxies != null) {
            return proxies;
        }
        proxies = new ArrayList<>();
        combinedCache.put(serviceClass, proxies);
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
            Set<ServiceProxy> factoryProxies = factory.createServiceProxies(serviceClass);
            if (factoryProxies == null) {
                factoryProxies = Collections.emptySet();
            }
            proxies.addAll(factoryProxies);
        }

        //拦截器按优先级排序
        Collections.sort(proxies, new Comparator<ServiceProxy>() {
            @Override
            public int compare(ServiceProxy s1, ServiceProxy s2) {
                return s2.priority() - s1.priority();
            }
        });
        return proxies;
    }


    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}
