package chao.android.tools.service_pools.cache;

import chao.java.tools.servicepool.IServiceFactory;
import chao.java.tools.servicepool.annotation.Service;
import chao.java.tools.servicepool.cache.custom.CustomCacheStrategy;

/**
 * @author luqin
 * @since 2020-08-06
 */
@Service(scope = 10)
public class Pool implements CustomCacheStrategy<IPoolInstance> {

    @Override
    public IPoolInstance getService(IServiceFactory factory, Class<IPoolInstance> originClass, Class<IPoolInstance> serviceClass) {
        return (IPoolInstance) factory.createInstance(serviceClass);
    }
}
