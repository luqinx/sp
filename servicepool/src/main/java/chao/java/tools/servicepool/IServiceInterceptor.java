package chao.java.tools.servicepool;

import java.lang.reflect.Method;

/**
 *
 * 拦截器
 *
 * @author luqin
 * @since 2020-07-12
 */
public interface IServiceInterceptor extends IService {
    void intercept(Class<? extends IService> originClass, IService source, Method method, Object[] args, IServiceInterceptorCallback callback);
}
