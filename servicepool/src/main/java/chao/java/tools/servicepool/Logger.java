package chao.java.tools.servicepool;

import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-08-06
 */
@Service(priority = IService.Priority.MIN_PRIORITY)
public class Logger implements ILogger {
    @Override
    public void e(String tag, String message) {
        System.err.println(message);
    }

    @Override
    public void w(String tag, String message) {
        System.out.println(message);
    }

    @Override
    public void d(String tag, String message) {
        System.out.println(message);
    }

    @Override
    public void i(String tag, String message) {
        System.out.println(message);
    }

    @Override
    public void v(String tag, String message) {
        System.out.println(message);
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
            if (!name.contains(Logger.class.getName())) {
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

        System.out.println(log.toString());
    }

    @Override
    public void log(String message, Object... args) {
        System.err.println(String.format(message, args));
    }

    @Override
    public void method() {

    }
}
