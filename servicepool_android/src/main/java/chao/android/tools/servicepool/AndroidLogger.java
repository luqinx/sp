package chao.android.tools.servicepool;

import android.util.Log;

import chao.java.tools.servicepool.ILogger;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-08-06
 */
@Service
public class AndroidLogger implements ILogger {

    private static final String TAG = "spa";

    @Override
    public void e(String tag, String message) {
        Log.e(tag, message);
    }

    @Override
    public void w(String tag, String message) {
        Log.w(tag, message);
    }

    @Override
    public void d(String tag, String message) {
        Log.d(tag, message);
    }

    @Override
    public void i(String tag, String message) {
        Log.i(tag, message);
    }

    @Override
    public void v(String tag, String message) {
        Log.v(tag, message);
    }

    @Override
    public void log(Object... messages) {
        StringBuilder log = new StringBuilder();

        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        String className = null;
        String method = null;
        for (StackTraceElement element: traces) {
            String name = element.getClassName();
            if (name.contains("dalvik") || name.contains("java.lang")) {
                continue;
            }
            if (!name.contains(AndroidLogger.class.getName())) {
                className = element.getClassName();
                className = className.substring(className.lastIndexOf(".") + 1);
                method = element.getMethodName();
                break;
            }
        }
        log.append(className).append(".").append(method).append("() ");
        for (Object message: messages) {
            log.append(message).append(", ");
        }
        Log.d(TAG, log.toString());
    }

    @Override
    public void log(String message, Object... args) {
        Log.d(TAG, String.format(message, args));
    }

    @Override
    public void method() {
        log();
    }
}
