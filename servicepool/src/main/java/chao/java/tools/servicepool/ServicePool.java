package chao.java.tools.servicepool;

import java.lang.reflect.Modifier;
import java.util.Map;

import chao.java.tools.servicepool.combine.CombineStrategy;
import chao.java.tools.servicepool.combine.CombineThreadExecutor;
import chao.java.tools.servicepool.debug.Debug;
import chao.java.tools.servicepool.event.EventManager;
import chao.java.tools.servicepool.event.EventService;

/**
 *
 * @author qinchao
 * @since 2019/4/29
 */
public class ServicePool {


    /**
     * 低优先级
     *
     * Deprecated replaced by {@link Priority#MIN}
     */
    @Deprecated
    public static final int MIN_PRIORITY = 0;

    /**
     * 普通优先级, 默认
     *
     * Deprecated replaced by {@link Priority#NORMAL}
     */
    @Deprecated
    public static final int NORMAL_PRIORITY = 3;

    /**
     * 高优先级
     *
     * Deprecated replaced by {@link Priority#MAX}
     */
    @Deprecated
    public static final int MAX_PRIORITY = 5;

    private static final String TAG = "sp";

    public interface Priority {
        /**
         * 低优先级
         */
        int MIN = 0;

        /**
         * 普通优先级, 默认
         */
        int NORMAL = 3;

        /**
         * 高优先级
         */
        int MAX = 5;
    }



    static final int SCOPE_MASK = 0xf0000000;

    /**
     *  全局缓存, 创建后不会被回收，直到进程结束
     *
     * Deprecated: replaced by {@link Scope#GLOBAL}
     *
     */
    @Deprecated
    public static final int SCOPE_GLOBAL = Scope.GLOBAL;

    /**
     *  临时缓存， 只要不被gc回收，服务对象一直存在
     *  如果被gc回收, 则重新创建
     *
     *  Deprecated: replaced by {@link Scope#WEAK}
     */
    @Deprecated
    public static final int SCOPE_WEAK = Scope.WEAK;

    /**
     *  临时缓存， 只要不被gc回收，服务对象一直存在
     *  如果被gc回收, 则重新创建
     *
     * Deprecated: replaced by {@link Scope#SOFT}
     */
    @Deprecated
    public static final int SCOPE_SOFT = Scope.SOFT;

    /**
     *  不会缓存， 每次获取都会重新创建
     *
     * Deprecated: replaced by {@link Scope#NORMAL}
     *  默认Scope
     */
    @Deprecated
    public static final int SCOPE_ONCE = Scope.NORMAL;


    public interface Scope {
        /**
         *  全局缓存, 创建后不会被回收，直到进程结束
         *
         */
        int GLOBAL = SCOPE_MASK;

        /**
         *  临时缓存， 只要不被gc回收，服务对象一直存在
         *  如果被gc回收, 则重新创建
         */
        int WEAK = SCOPE_MASK | 1;

        /**
         *  临时缓存， 只要不被gc回收，服务对象一直存在
         *  如果被gc回收, 则重新创建
         */
        int SOFT = SCOPE_MASK | 2;

        /**
         *  不会缓存， 每次获取都会重新创建
         *
         *  默认Scope
         */
        int NORMAL = SCOPE_MASK | 3;

    }



    protected static DefaultServiceController controller;

    private volatile static boolean loaded = false;

    public static ExceptionHandler exceptionHandler;

    public static ILogger logger = new Logger();

    private static EventManager eventManager;

    public static CombineThreadExecutor executor;

    private static NoOpInstanceFactory noOpFactory;

    public static boolean isLoaded() {
        return loaded;
    }

    /**
     * 加载
     */
    public static void loadInitService() {
        executor = new CombineThreadExecutor();
        eventManager = new EventManager();
        InitServiceManager initServices = getService(InitServiceManager.class);
        try {
            if (initServices != null) {
                initServices.initService();
            } else {
                throw new NullPointerException();
            }
        } catch (Throwable e) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(e, e.getMessage());
            }
        }
    }

    public static synchronized void loadServices() {
        if (loaded) {
            return;
        }
        controller = new DefaultServiceController();

        noOpFactory = new NoOpInstanceFactory();

        long start = System.currentTimeMillis();
        IServiceLoader serviceLoader = controller.getServiceByClass(IServiceLoader.class);
        if (serviceLoader == null) {
            serviceLoader = new BuildInServiceLoader();
        }
        controller.addServices(serviceLoader.getServices());
        long end = System.currentTimeMillis();
        logger.log("service loader spent:" + (end - start));

        //todo temp for online test.
        for (Throwable t: Debug.throwables()) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(t, t.getMessage());
            }
        }
        for (String error: Debug.errors()) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(null, error);
            }
        }

//        IServiceFactories factories = controller.getServiceByClass(IServiceFactories.class);
//        if (factories == null || factories instanceof NoOpInstance) {
//            Debug.addError("IServiceFactories not found !!!");
//            throw new ServicePoolException("sp internal err.");
//        }
//        controller.addFactories(factories);
        controller.loadFinished();
        loaded = true;

        for (Throwable t: Debug.throwables()) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(t, t.getMessage());
            }
        }
        for (String error: Debug.errors()) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(null, error);
            }
        }

    }

    /**
     * 获取service实例对象
     *
     * @param serviceClass  可以是interface
     * @param <T>   service实例对象类型
     * @return  service实例对象, 如果没有获取到具体的实现，
     *          会通过 {@link NoOpInstanceFactory} 返回一个 {@link NoOpInstance} Mock实例
     */
    public static <T extends IService> T getService(Class<T> serviceClass) {
        T instance = null;
        if (!serviceClass.isInterface() && !Modifier.isAbstract(serviceClass.getModifiers())) {
//            throw new ServicePoolException("no-interface/no-abstract class should call method getFixedService.");
            logger.e(TAG, "no-interface/no-abstract class should call method getFixedService.");
            return getFixedService(serviceClass); //兼容老版本, 本应该直接抛异常
        }
        try {
            checkLoader();
            instance = controller.getServiceByClass(serviceClass);
        } catch (Throwable e) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(e, String.valueOf(serviceClass));
            }
        }
        if (instance == null) {
            return noOpFactory.newInstance(serviceClass);
        }
        return instance;
    }

    /**
     * 指定返回service对象类型, 获取service实例对象
     * 说明: 在一个接口有多个实现类的场景下, 通过指定tClass来明确使用哪个实现
     *
     * @param defaultService 如果没有获取到实现类返回defaultService
     * @param <T>   service实例对象类型
     * @return  service实例对象
     */
    public static <T extends IService> T getService(Class<T> tClass, T defaultService) {
        if (!tClass.isInterface() && !Modifier.isAbstract(tClass.getModifiers())) {
//            throw new ServicePoolException("no-interface/no-abstract class should call method getFixedService.");
            logger.e(TAG, "no-interface/no-abstract class should call method getFixedService.");
            return getFixedService(tClass, defaultService); //兼容老版本, 本应该直接抛异常

        }
        try {
            checkLoader();
            return controller.getServiceByClass(tClass, defaultService);
        } catch (Throwable e) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(e, String.valueOf(tClass));
            }
        }
        return defaultService;
    }

    /**
     * 根据serviceClass获取service实例
     *
     * 服务实例class和serviceClass一致，不是它的子类
     *
     * @param serviceClass  只可以是class
     * @param <T> service实例对象类型
     * @return  返回serviceClass类的服务实例
     */
    public static <T extends IService> T getFixedService(Class<T> serviceClass) {
        if (serviceClass.isInterface() || Modifier.isAbstract(serviceClass.getModifiers())) {
            throw new ServicePoolException("interface/abstract class should call method getService.");
        }
        T instance = null;
        try {
            checkLoader();
            instance = controller.getFixedServiceByClass(serviceClass);
        } catch (Throwable e) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(e, String.valueOf(serviceClass));
            }
        }
        if (instance == null) {
            return noOpFactory.newInstance(serviceClass);
        }
        return instance;
    }

    public static <T extends IService> T getFixedService(Class<T> serviceClass, T defaultService) {
        if (serviceClass.isInterface() || Modifier.isAbstract(serviceClass.getModifiers())) {
            throw new ServicePoolException("interface/abstract class should call method getService.");
        }
        T instance = null;
        try {
            checkLoader();
            instance = controller.getFixedServiceByClass(serviceClass);
        } catch (Throwable e) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(e, String.valueOf(serviceClass));
            }
        }
        if (instance == null) {
            return defaultService;
        }
        return instance;
    }

    public static <T extends IService> T getService(String path) {
        Class<? extends IService> clazz = controller.getServiceByPath(path);
        if (clazz == null) {
            return null;
        }
        return (T) getFixedService(clazz);
    }


    public static void registerPaths(Map<String, Class<? extends IService>> serviceMaps) {
        try {
            checkLoader();
            IPathService pathService = controller.getPathService();
            if (serviceMaps == null) {
                return;
            }
            for (String path : serviceMaps.keySet()) {
                pathService.put(path, serviceMaps.get(path));
            }
        } catch (Throwable e) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(e, e.getMessage());
            }
        }
    }

    public static void recycleService(Class clazz) {
        controller.recycleService(clazz);
    }

//    public static <T extends IService> T newService(Class<T> serviceClass) {
//        checkLoader();
//        return controller.newService(serviceClass);
//    }

    protected static void checkLoader() {
        if (controller == null) {
            synchronized (ServicePool.class) {
                if (controller == null) {
                    long start = System.currentTimeMillis();
                    try {
                        loadServices();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        if (exceptionHandler != null) {
                            exceptionHandler.onException(e, e.getMessage());
                        }
                    }
                    long end = System.currentTimeMillis();
                    logger.log("load init services, spent:" + (end - start));
                }
            }
        }
    }

    public static ServiceProxy getProxy(Class<? extends IService> clazz) {
        try {
            checkLoader();
            return controller.getProxy(clazz);
        } catch (Throwable e) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(e, String.valueOf(clazz));
            }
        }
        return null;
    }

    public static void setExceptionHandler(ExceptionHandler _exceptionHandler) {
        exceptionHandler = _exceptionHandler;
    }

    public static void registerEventService(EventService eventService) {
        eventManager.registerEventService(eventService);
    }

    public static <T extends EventService> T getEventService(Class<T> eventClazz) {
        return eventManager.getEventService(eventClazz);
    }

    public static <T extends IService> T getCombineService(Class<T> combineClass) {
        return controller.getCombineService(combineClass);
    }

    public static <T extends IService> T getCombineService(Class<T> combineClass, CombineStrategy strategy) {
        return controller.getCombineService(combineClass, strategy);
    }

    public static <T extends IService> void cacheService(Class<T> serviceClass, InnerProxy<T> innerProxy) {
        controller.cacheService(serviceClass, innerProxy);
    }
}
