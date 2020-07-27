package chao.android.tools.servicepool.rpc;

import java.lang.reflect.Method;
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

    public RemoteClientMethod(Method method, IServiceInterceptorCallback callback) {
        this.method = method;
        this.callback = callback;
        this.countDownLatch = new CountDownLatch(1);
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
