package chao.android.tools.service_pools.serviceinterceptor;

import java.lang.reflect.Method;

import chao.java.tools.servicepool.IServiceInterceptor;
import chao.java.tools.servicepool.IServiceInterceptorCallback;
import chao.java.tools.servicepool.annotation.Service;

import static chao.java.tools.servicepool.ServicePool.logger;

/**
 * @author luqin
 * @since 2020-07-12
 */
@Service
public class NormalServiceInterceptor implements IServiceInterceptor {
    @Override
    public void intercept(Object source, Method method, Object[] args, IServiceInterceptorCallback callback) {
//        logger.log(source.toString() + ": " + method.getName());
        callback.onContinue(null, method, args);
    }
}
