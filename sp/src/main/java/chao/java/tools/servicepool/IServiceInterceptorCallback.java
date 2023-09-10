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

    /**
     * @param method next method
     * 继续执行下一个拦截策略， 如果没有下一个拦截，执行源对象方法
     * @param args args
     */
    void onContinue(Method method, Object... args);

    /**
     *  拦截，并返回指定result
     *
     * @param result 拦截返回值
     */
    void onInterrupt(Object result);
}
