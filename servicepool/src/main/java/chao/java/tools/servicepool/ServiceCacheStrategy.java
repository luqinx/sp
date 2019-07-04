package chao.java.tools.servicepool;

import java.lang.ref.WeakReference;

/**
 * Service缓存策略
 *
 * @author qinchao
 * @since 2019/6/25
 */
public interface ServiceCacheStrategy {

    IService getService(Class<? extends IService> serviceClass);

    /**
     *  全局策略， 类似一个单例
     */
    class Global implements ServiceCacheStrategy {

        private IService service;

        @Override
        public IService getService(Class<? extends IService> serviceClass) {
            if (service == null) {
                synchronized (this) {
                    if (service == null) {
                        service = ReflectUtil.newInstance(serviceClass);
                    }
                }
            }
            return service;
        }
    }

    /**
     *  临时策略， 如果不被gc回收，则不会重新创建
     */
    class Temp implements ServiceCacheStrategy {

        private WeakReference<? extends IService> weakService;

        @Override
        public IService getService(Class<? extends IService> serviceClass) {
            if (weakService == null || weakService.get() == null) {
                synchronized (this) {
                    if (weakService == null || weakService.get() == null) {
                        weakService = new WeakReference<>(ReflectUtil.newInstance(serviceClass));
                    }
                }
            }
            return weakService.get();
        }
    }

    /**
     *  每次获取时创建一个新的对象
     */
    class Once implements ServiceCacheStrategy {

        @Override
        public IService getService(Class<? extends IService> serviceClass) {
            return ReflectUtil.newInstance(serviceClass);
        }
    }
}
