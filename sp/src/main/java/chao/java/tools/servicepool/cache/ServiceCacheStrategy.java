package chao.java.tools.servicepool.cache;

import chao.java.tools.servicepool.IService;

/**
 * Service缓存策略
 *
 * @author qinchao
 * @since 2019/6/25
 */
public interface ServiceCacheStrategy<T extends IService> {
    T getService(Class<T> serviceClass, Class<T> originClass);
}
