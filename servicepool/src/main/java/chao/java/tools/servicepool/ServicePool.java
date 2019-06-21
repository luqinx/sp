package chao.java.tools.servicepool;

/**
 * todo 初始化 & 初始化优先级
 * todo 懒加载 lazy load
 * todo 初始化时机, 启动初始化，首页初始化(android), 按需初始化...
 * todo 缓存策略， 全局缓存, 定时缓存, 不缓存
 * todo 多行程同步问题
 *
 * @author qinchao
 * @since 2019/4/29
 */
public class ServicePool {

    private static DefaultServiceController controller;

    private volatile static boolean loaded = false;

    public static synchronized void loadServices() {
        if (loaded) {
            return;
        }
        loaded = true;
        controller = new DefaultServiceController();
        ServiceLoader<IService> loader = ServiceLoader.load(IService.class);
        controller.addServices(loader.getServices());

        ServiceLoader<IServiceFactory> lazyFactories = ServiceLoader.load(IServiceFactory.class);
        for (Class<? extends IServiceFactory> factoryClass: lazyFactories) {
            try {
                IServiceFactory serviceFactory = factoryClass.newInstance();
                controller.addServices(serviceFactory.createServices());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        controller.loadFinished();
    }

    public static <T extends IService> T getService(Class<T> serviceClass) {
        checkLoader();
        return controller.getServiceByClass(serviceClass, serviceClass);
    }

    public static <T extends IService> T getService(Class serviceClass, Class<T> tClass) {
        checkLoader();
        return controller.getServiceByClass(serviceClass, tClass);
    }

    public static <T extends IService> T getService(Class serviceClass, Class<T> tClass, T service) {
        checkLoader();
        return controller.getServiceByClass(serviceClass, tClass, service);
    }

//    public static <T extends IService> T newService(Class<T> serviceClass) {
//        checkLoader();
//        return controller.newService(serviceClass);
//    }

    private static synchronized void checkLoader() {
        if (controller == null) {
            loadServices();
        }
    }
}
