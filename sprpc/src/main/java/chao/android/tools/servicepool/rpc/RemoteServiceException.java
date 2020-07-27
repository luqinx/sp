package chao.android.tools.servicepool.rpc;

/**
 * @author luqin
 * @since 2020-07-23
 */
public class RemoteServiceException  extends RuntimeException {
    public RemoteServiceException() {
        super();
    }

    public RemoteServiceException(String message) {
        super(message);
    }

    public RemoteServiceException(String message, Throwable e) {
        super(message, e);
    }
}
