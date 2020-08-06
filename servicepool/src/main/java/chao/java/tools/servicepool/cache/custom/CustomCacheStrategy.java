package chao.java.tools.servicepool.cache.custom;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactory;

/**
 *
 *
 *
 *
 * @author luqin
 * @since 2020-08-06
 */
public interface CustomCacheStrategy<T extends IService> extends IService {
    T getService(IServiceFactory factory, Class<T> originClass, Class<T> serviceClass);
}
