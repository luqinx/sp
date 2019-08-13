package chao.java.tools.servicepool;

import chao.java.tools.servicepool.annotation.Service;

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


    public static boolean isLoaded() {
        return loaded;
    }

    public static synchronized void loadServices() {
        if (loaded) {
            return;
        }
        loaded = true;
        controller = new DefaultServiceController();
        long start = System.currentTimeMillis();
        ServiceLoader<IService> loader = ServiceLoader.load(IService.class);
        long end = System.currentTimeMillis();

        System.out.println("service loader spent:" + (end - start));
        controller.addServices(loader.getServices());
        ServiceFactories factories = controller.getServiceByClass(ServiceFactories.class);
        if (factories == null) {
            throw new ServicePoolException("sp internal err.");
        }
        controller.addFactories(factories);
        controller.loadFinished();
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
        checkLoader();
        return controller.getServiceByClass(serviceClass);
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
        checkLoader();
        return controller.getServiceByClass(tClass, defaultService);
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
                    System.out.println("load init services, spent:" + (end - start));
                }
            }
        }
    }

    public static ServiceProxy getProxy(Class<?> clazz) {
        checkLoader();
        return controller.getProxy(clazz);
    }
}
