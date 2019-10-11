package chao.android.tools.servicepool.route;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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
public class RouteCombineStrategyImpl implements CombineStrategy, Handler.Callback {

    private static final int MSG_WHAT_CONTINUE = 1;

    private static final int MSG_WHAT_INTERRUPT = 2;

    //callback返回时应该使用调用线程
    private Handler mHandler = new Handler(Looper.myLooper(), this);

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
                post.callback = callback;

                Message msg = mHandler.obtainMessage();

                CancelableCountDownLatch countDownLatch = new CancelableCountDownLatch(proxies.size());
                try {
                    execute(proxies, 0, countDownLatch, method, post, callback);
                    countDownLatch.await(route.interceptorTimeout, TimeUnit.SECONDS);

                    if (countDownLatch.getCount() > 0) {
                        post.e = new ServicePoolException("The interceptor processing timed out");
                        post.code = RouteArgs.INTERCEPTOR_CODE_TIMEOUT;

                        msg.what = MSG_WHAT_INTERRUPT;

                    } else if (post.code != RouteArgs.INTERCEPTOR_CODE_OK){
                        msg.what= MSG_WHAT_INTERRUPT;
                    } else {
                        msg.what = MSG_WHAT_CONTINUE;
                    }
                } catch (Throwable e) {
                    post.code = RouteArgs.INTERCEPTOR_CODE_ERR;
                    msg.what= MSG_WHAT_INTERRUPT;
                }
                msg.obj = post;
                msg.sendToTarget();

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

    @Override
    public boolean handleMessage(Message msg) {
        RouteArgs post = (RouteArgs) msg.obj;
        switch (msg.what) {
            case MSG_WHAT_INTERRUPT:
                if (post != null) {
                    post.callback.onInterrupt(post.e);
                }
                break;
            case MSG_WHAT_CONTINUE:
                if (post != null) {
                    post.callback.onContinue(post.route);
                }
                break;
        }
        return true;
    }
}
