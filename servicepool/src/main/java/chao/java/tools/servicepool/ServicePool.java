package chao.java.tools.servicepool;

import java.util.Map;

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

    protected static DefaultServiceController controller;

    private volatile static boolean loaded = false;

    private static ExceptionHandler exceptionHandler;

    public static ILogger logger = new Logger();

    private static EventManager eventManager = new EventManager();

    public static CombineThreadExecutor executor;

    public static boolean isLoaded() {
        return loaded;
    }

    /**
     * 加载
     */
    public static void loadInitService() {
        executor = new CombineThreadExecutor();
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
        long start = System.currentTimeMillis();
        ServiceLoader<IService> loader = ServiceLoader.load(IService.class);
        long end = System.currentTimeMillis();

        logger.log("service loader spent:" + (end - start));
        controller.addServices(loader.getServices());

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
     * @param serviceClass  可以是interface，也可以是class
     * @param <T>   service实例对象类型
     * @return  service实例对象, 如果没有获取到具体的实现，
     *          会通过 {@link NoOpInstanceFactory} 返回一个 {@link NoOpInstance} Mock实例
     */
    public static <T extends IService> T getService(Class<T> serviceClass) {
        try {
            checkLoader();
            return controller.getServiceByClass(serviceClass);
        } catch (Throwable e) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(e, String.valueOf(serviceClass));
            }
        }
        return null;
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
        try {
            checkLoader();
            return controller.getServiceByClass(tClass, defaultService);
        } catch (Throwable e) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(e, String.valueOf(tClass));
            }
        }
        return null;
    }

    public static <T> T getService(String path) {
        Class<? extends IService> clazz = controller.getServiceByPath(path);
        if (clazz == null) {
            return null;
        }
        return (T) getService(clazz);
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

    private static void checkLoader() {
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

    public static ServiceProxy getProxy(Class<?> clazz) {
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
}
