package chao.java.tools.servicepool.cache;

import java.lang.reflect.Method;

import chao.android.tools.interceptor.Interceptor;
import chao.android.tools.interceptor.OnInvoke;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceInterceptor;
import chao.java.tools.servicepool.IServiceInterceptorCallback;
import chao.java.tools.servicepool.ServiceInterceptorStrategy;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.combine.CombineService;

abstract class AbsServiceCacheStrategy<T extends IService> implements ServiceCacheStrategy<T> {

    private static ServiceInterceptorStrategy strategy = new ServiceInterceptorStrategy();


    protected T getProxyService(final Class<T> originClazz, final T instance) {
        if (originClazz != null && originClazz.isInterface()) {
            return Interceptor.of(instance, originClazz).intercepted(true).invoke(new OnInvoke<T>() {

                @Override
                public Object onInvoke(T source, final Method method, final Object[] args) {
                    final ResultHolder holder = new ResultHolder();

                    CombineService combineService = (CombineService) ServicePool.getCombineService(IServiceInterceptor.class, strategy);
                    if (combineService.size() == 0) {
                        try {
                            holder.result = method.invoke(instance, args);
                        } catch (Throwable e) {
                            if (ServicePool.exceptionHandler != null) {
                                ServicePool.exceptionHandler.onException(e, e.getMessage());
                            }
                            e.printStackTrace();
                        }
                    } else {
                        IServiceInterceptor interceptor = (IServiceInterceptor) combineService;
                        interceptor.intercept(originClazz, source, method, args, new IServiceInterceptorCallback() {
                            @Override
                            public void onContinue(Method interceptorMethod, Object... interceptorArgs) {
                                try {
                                    holder.result = method.invoke(instance, args);
                                } catch (Throwable e) {
                                    if (ServicePool.exceptionHandler != null) {
                                        ServicePool.exceptionHandler.onException(e, e.getMessage());
                                    }
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onInterrupt(Object lastResult) {
                                holder.result = lastResult;
                            }
                        });
                    }
                    return holder.result;
                }

            }).newInstance();
        } else {
            return instance;
        }
    }

    private class ResultHolder {
        Object result;
    }
}