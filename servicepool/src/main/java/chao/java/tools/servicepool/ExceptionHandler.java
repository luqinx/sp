package chao.java.tools.servicepool;

/**
 * @author luqin
 * @since 2019-08-20
 */
public interface ExceptionHandler {
    void onException(Throwable e, Class<?> service);
}
