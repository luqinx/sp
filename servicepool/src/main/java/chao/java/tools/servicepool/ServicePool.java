package chao.java.tools.servicepool;

import chao.java.tools.servicepool.debug.Debug;
import chao.java.tools.servicepool.event.EventManager;
import chao.java.tools.servicepool.event.EventService;

/**
 * todo 多行程同步问题
 *
 * @author qinchao
 * @since 2019/4/29
 */
public class ServicePool {

    private static DefaultServiceController controller;

    private volatile static boolean loaded = false;

    private static ExceptionHandler exceptionHandler;

    public static ILogger logger = new Logger();

    private static EventManager eventManager = new EventManager();

    public static boolean isLoaded() {
        return loaded;
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
        ServiceFactories factories = controller.getServiceByClass(ServiceFactories.class);
        if (factories == null) {
            throw new ServicePoolException("sp internal err.");
        }
        controller.addFactories(factories);
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
    public static <T> T getService(Class<T> serviceClass) {
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

//    public static <T extends IService> T newService(Class<T> serviceClass) {
//        checkLoader();
//        return controller.newService(serviceClass);
//    }

    private static void checkLoader() {
        if (controller == null) {
            synchronized (ServicePool.class) {
                if (controller == null) {
                    long start = System.currentTimeMillis();
                    loadServices();
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
}
