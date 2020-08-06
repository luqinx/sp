package chao.android.tools.rpc;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import chao.java.tools.servicepool.IServiceInterceptorCallback;

/**
 * @author luqin
 * @since 2020-07-27
 */
public class RemoteClientMethod {

    Method method;

    IServiceInterceptorCallback callback;

    private CountDownLatch countDownLatch;

    RemoteCallbackHandler callbackHandler;

    Type callbackResolveType;

    public RemoteClientMethod(Method method, Object[] args, IServiceInterceptorCallback callback) {
        this.method = method;
        this.callback = callback;
        this.countDownLatch = new CountDownLatch(1);
        if (args != null && args.length > 0 && (args[args.length -1] instanceof RemoteCallbackHandler)) {
            callbackHandler = (RemoteCallbackHandler) args[args.length - 1];

            Type[] types = method.getGenericParameterTypes();
            if (types[types.length - 1] instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) types[types.length - 1];
                callbackResolveType = pt.getActualTypeArguments()[0];
            }
        }
    }

    public void await(long milliseconds) throws InterruptedException {
        countDownLatch.await(milliseconds, TimeUnit.MILLISECONDS);
    }

    public void countDown() {
        countDownLatch.countDown();
    }

    public boolean isCountDown() {
        return countDownLatch.getCount() == 0;
    }
}
