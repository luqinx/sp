package chao.java.tools.servicepool;

/**
 * @author qinchao
 * @since 2019/5/3
 */
public class ServicePoolException extends RuntimeException {

    public ServicePoolException(String message, Object... args) {
        super(String.format(message, args));
    }

    public ServicePoolException(Throwable t, String message, Object... args) {
        super(String.format(message, args), t);
    }
}
