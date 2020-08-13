package chao.android.tools.service_pools.serviceinterceptor;

import java.lang.reflect.Method;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceInterceptor;
import chao.java.tools.servicepool.IServiceInterceptorCallback;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-08-12
 */
@Service
public class ServiceDirectReturnInterceptor implements IServiceInterceptor {
    @Override
    public void intercept(Class<? extends IService> originClass, IService source, Method method, Object[] args, IServiceInterceptorCallback callback) {
        return;
    }
}
