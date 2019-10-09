package chao.java.tools.servicepool.combine;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import chao.java.tools.servicepool.ILogger;
import chao.java.tools.servicepool.Logger;

/**
 * @author luqin
 * @since 2019-10-08
 */
public class CombineThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;

    private final ThreadGroup group;

    private ILogger logger = new Logger();

    public CombineThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "Combine task pool No." + poolNumber.getAndIncrement() + ", thread No.";
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String threadName = namePrefix + threadNumber.getAndIncrement();
        Thread thread = new Thread(group, runnable, threadName, 0);
        if (thread.isDaemon()) {   //设为非后台线程
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) { //优先级为normal
            thread.setPriority(Thread.NORM_PRIORITY);
        }

        // 捕获多线程处理中的异常
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                logger.log( "Combine Running task appeared exception! Thread [" + thread.getName() + "], because [" + ex.getMessage() + "]");
            }
        });
        return thread;
    }
}
