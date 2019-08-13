package chao.java.tools.servicepool;

/**
 * @author luqin
 * @since 2019-08-06
 */
public interface ILogger {
    void e(String tag, String message);
    void w(String tag, String message);
    void d(String tag, String message);
    void i(String tag, String message);
    void v(String tag, String message);
    void log(Object... messages);
    void log(String message, Object... args);
    void method();
}
