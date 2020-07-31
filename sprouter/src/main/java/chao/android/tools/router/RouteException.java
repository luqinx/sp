package chao.android.tools.router;

/**
 * @author luqin
 * @since 2020-07-28
 */
public class RouteException extends RuntimeException {
    public RouteException(String m) {
        super(m);
    }

    public RouteException(String m, Throwable e) {
        super(m, e);
    }
}
