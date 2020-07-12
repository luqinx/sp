package chao.java.tools.servicepool;

import java.lang.reflect.Method;

/**
 *
 * 拦截器
 *
 * @author luqin
 * @since 2020-07-12
 */
public interface IServiceInterceptorCallback {
    void onContinue(Method method, Object... args);
    void onInterrupt(Object result);
}
