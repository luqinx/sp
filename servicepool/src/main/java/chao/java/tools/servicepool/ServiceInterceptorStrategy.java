package chao.java.tools.servicepool;

import java.lang.reflect.InvocationTargetException;
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
     * @param method    将执行的方法
     *                  {@link IServiceInterceptor#intercept)}
     *
     * @param args      执行方法的参数
     *                  (Object, Method, Object[], IServiceInterceptorCallback)
     *
     * @return
     */
    @Override
    public boolean invoke(List<ServiceProxy> proxies, Class serviceClass, Method method, Object[] args) {
        if (proxies.size() > 0) {
            IServiceInterceptorCallback callback = (IServiceInterceptorCallback) args[3];
            ResultHolder holder = new ResultHolder();
            holder.callback = callback;

            execute(proxies, 0, method, args, holder);
            if (callback != null && !holder.intercept) {
                callback.onContinue(method, args);
            }
        } else {
            IServiceInterceptorCallback callback = (IServiceInterceptorCallback) args[3];
            if (callback != null) {
                callback.onContinue(method, args);
            }
        }
        return true;
    }

    private ResultHolder execute(final List<ServiceProxy> proxies, final int index, final Method method, final Object[] args, final ResultHolder holder) {
        if (index >= proxies.size()) {
            return holder;
        }
        try {
            ServiceProxy proxy = proxies.get(index);
            Object source = args[0];
            Method sourceMethod = (Method) args[1];
            Object sourceArgs = args[2];

            method.invoke(proxy.getService(), source, sourceMethod, sourceArgs, new IServiceInterceptorCallback() {
                @Override
                public void onContinue(Method sourceMethod, Object... sourceArgs) {
                    execute(proxies, index + 1, method, args, holder);
                }

                @Override
                public void onInterrupt(Object result) {
                    holder.result = result;
                    holder.intercept = true;
                    holder.callback.onInterrupt(result);
                }
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return holder;
    }

    private class ResultHolder {
        Object result;
        boolean intercept;
        IServiceInterceptorCallback callback;
    }
}
