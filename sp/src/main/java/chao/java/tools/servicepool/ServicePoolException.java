package chao.java.tools.servicepool;

/**
 * @author qinchao
 * @since 2019/5/3
 */
public class ServicePoolException extends RuntimeException {

    public ServicePoolException(String message) {
        super(message);
    }

    public ServicePoolException(String message, Throwable t) {
        super(message, t);
    }
}
