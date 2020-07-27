package chao.java.tools.servicepool.combine;

import java.lang.reflect.Method;
import java.util.List;

import chao.java.tools.servicepool.ServicePoolException;
import chao.java.tools.servicepool.ServiceProxy;

/**
 * @author luqin
 * @since 2020-07-26
 */
public class DefaultCombineStrategy implements CombineStrategy {
    @Override
    public boolean filter(Class serviceClass, Method method, Object[] args) {
        return true;
    }

    @Override
    public Object invoke(List<ServiceProxy> proxies, Class serviceClass, Method method, Object[] args) {
        Object result = null;
        for (ServiceProxy proxy: proxies) {
            try {
                result = method.invoke(proxy.getService(), args);
            } catch (Throwable e) {
                throw new ServicePoolException(e.getMessage(), e);
            }
        }
        return result;
    }
}
