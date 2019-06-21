package chao.java.tools.servicepool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class ServiceThreadFactory implements ThreadFactory {

    private final AtomicInteger mCount = new AtomicInteger(1);

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, "servicepool-" + mCount.getAndIncrement());
    }
}
