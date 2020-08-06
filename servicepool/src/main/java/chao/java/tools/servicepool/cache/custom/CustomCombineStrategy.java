package chao.java.tools.servicepool.cache.custom;

import java.lang.reflect.Method;
import java.util.List;

import chao.java.tools.servicepool.ServicePoolException;
import chao.java.tools.servicepool.ServiceProxy;
import chao.java.tools.servicepool.combine.CombineStrategy;

/**
 * @author luqin
 * @since 2020-08-06
 */
public class CustomCombineStrategy implements CombineStrategy {

    private int customScope;

    public CustomCombineStrategy(int customScope) {
        this.customScope = customScope;
    }

    @Override
    public boolean filter(Class serviceClass, Method method, Object[] args) {
        return CustomCacheStrategy.class.isAssignableFrom(serviceClass);
    }

    @Override
    public Object invoke(List<ServiceProxy> proxies, Class serviceClass, Method method, Object[] args) {

        if (proxies.size() == 0) {
            return null;
        }
        //最多能有一个
        int count = 0;
        for (ServiceProxy proxy: proxies) {
            if (proxy.realScope() != customScope) {
                continue;
            }
            count++;
            if (count > 1) {
                throw new ServicePoolException("more than one custom cache strategy has the same customScope: " + customScope);
            }
            try {
                return method.invoke(proxy.getService(), args);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
