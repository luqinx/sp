package chao.java.tools.servicepool;

import java.lang.reflect.Method;
import java.util.List;

import chao.java.tools.servicepool.combine.CombineStrategy;

/**
 *
 * 同步拦截策略
 *
 * 每个拦截器按次序依次执行拦截操作,
 *
 * 回调接口IServiceInterceptor只能通过同步方式回调onContinue/onIntercept方法, 异步会导致操作异常
 *
 * @author luqin
 * @since 2020-07-12
 */
public class ServiceInterceptorStrategy implements CombineStrategy {
    @Override
    public boolean filter(Class serviceClass, Method method, Object[] args) {
        return IServiceInterceptor.class.isAssignableFrom(serviceClass);
    }

    /**
     *
     * @param proxies   service组的代理, 已经根据优先级排序
     *
     * @param serviceClass  service类 {@link IServiceInterceptor}
     *
     * @param method    将执行的方法 {@link IServiceInterceptor#intercept(Class, IService, Method, Object[], IServiceInterceptorCallback)} )}
     *
     * @param args      执行方法的参数
     *                  (Object, Method, Object[], IServiceInterceptorCallback)
     *
     * @return result
     */
    @Override
    public Object invoke(List<ServiceProxy> proxies, Class serviceClass, Method method, Object[] args) {
        IServiceInterceptorCallback callback = (IServiceInterceptorCallback) args[4];
        ResultHolder holder = new ResultHolder();
        holder.callback = callback;
        holder.interceptorCursor = 0;
//        if (proxies.size() > 0) {
//            execute(proxies, holder.interceptorCursor, method, args, holder);
//        }
        //确保所有拦截器被执行
        while (proxies.size() > 0 && !holder.intercept && holder.interceptorCursor < proxies.size()) {
            execute(proxies, holder.interceptorCursor, method, args, holder);
            holder.interceptorCursor++;
        }

        if (callback != null && !holder.intercept) {
            callback.onContinue(method, args);
        }
        return null;
    }

    private void execute(final List<ServiceProxy> proxies, int index, final Method method, final Object[] args, final ResultHolder holder) {
        if (index >= proxies.size()) {
            return;
        }
        try {
            ServiceProxy proxy = proxies.get(index);
            Class<?> sourceClass = (Class<?>) args[0];
            Object source = args[1];
            Method sourceMethod = (Method) args[2];
            Object sourceArgs = args[3];

            method.invoke(proxy.getService(), sourceClass, source, sourceMethod, sourceArgs, new IServiceInterceptorCallback() {
                @Override
                public void onContinue(Method sourceMethod, Object... sourceArgs) {
                    //支持通过拦截修改方法和参数
                    args[2] = sourceMethod;
                    args[3] = sourceArgs;
                    execute(proxies, ++holder.interceptorCursor , method, args, holder);
                }

                @Override
                public void onInterrupt(Object result) {
                    holder.intercept = true;
                    holder.callback.onInterrupt(result);
                }
            });
        } catch (Throwable e) {
            throw new ServicePoolException(e.getMessage(), e);
        }
    }

    private class ResultHolder {
        int interceptorCursor;
        boolean intercept;
        IServiceInterceptorCallback callback;
    }
}
