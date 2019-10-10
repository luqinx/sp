package chao.android.tools.servicepool.route;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.ServicePoolException;
import chao.java.tools.servicepool.ServiceProxy;
import chao.java.tools.servicepool.annotation.Service;
import chao.java.tools.servicepool.combine.CombineStrategy;
import chao.java.tools.servicepool.thirdparty.CancelableCountDownLatch;

/**
 * @author luqin
 * @since 2019-10-08
 */
@Service(priority = IService.Priority.MIN_PRIORITY, scope = IService.Scope.global)
public class RouteCombineStrategyImpl implements CombineStrategy {

    @Override
    public boolean filter(Class serviceClass, Method method, Object[] args) {
        if (!(RouteInterceptor.class.isAssignableFrom(serviceClass))) {
            return false;
        }
        return args != null && args.length == 2;
    }

    @Override
    public boolean invoke(final List<ServiceProxy> proxies, Class serviceClass, final Method method, final Object[] args) {


        ServicePool.executor.execute(new Runnable() {
            @Override
            public void run() {
                RouteBuilder route = (RouteBuilder) args[0];
                RouteArgs post = new RouteArgs(route);
                RouteInterceptorCallback callback = (RouteInterceptorCallback) args[1];

                CancelableCountDownLatch countDownLatch = new CancelableCountDownLatch(proxies.size());
                try {
                    execute(proxies, 0, countDownLatch, method, post, callback);
                    countDownLatch.await(route.interceptorTimeout, TimeUnit.SECONDS);
                    if (countDownLatch.getCount() > 0) {
                        Throwable e = new ServicePoolException("The interceptor processing timed out");
                        post.e = e;
                        post.code = RouteArgs.INTERCEPTOR_CODE_TIMEOUT;
                        callback.onInterrupt(e);
                    } else if (post.code != RouteArgs.INTERCEPTOR_CODE_OK){
                        callback.onInterrupt(post.e);
                    } else {
                        callback.onContinue(route);
                    }
                } catch (Throwable e) {
                    post.code = RouteArgs.INTERCEPTOR_CODE_ERR;
                    callback.onInterrupt(e);
                }

            }
        });
        return true;
    }

    private void execute(final List<ServiceProxy> proxies, final int index, final CancelableCountDownLatch countDownLatch, final Method method, final RouteArgs post, final RouteInterceptorCallback callback) throws InvocationTargetException, IllegalAccessException {
        if (index >= proxies.size()) {
            return;
        }

        ServiceProxy proxy = proxies.get(index);
        method.invoke(proxy.getService(), post.route, new RouteInterceptorCallback() {
            @Override
            public void onContinue(RouteBuilder route) {
                countDownLatch.countDown();
                try {
                    execute(proxies, index + 1, countDownLatch, method, post, callback);
                } catch (Throwable e) {
                    post.e = e;
                    post.message = e.getMessage();
                    post.code = RouteArgs.INTERCEPTOR_CODE_ERR;
                    countDownLatch.cancel();
                }
            }

            @Override
            public void onInterrupt(Throwable e) {
                post.e = e;
                if (e != null) post.message = e.getMessage();
                post.code = RouteArgs.INTERCEPTOR_CODE_USER;
                countDownLatch.cancel();
            }
        });
    }

}
