package chao.android.tools.servicepool.route;

/**
 * @author luqin
 * @since 2019-10-09
 */
public class RouteArgs {

    static final int INTERCEPTOR_CODE_USER = -2;

    static final int INTERCEPTOR_CODE_ERR = -1;

    static final int INTERCEPTOR_CODE_TIMEOUT = -3;

    static final int INTERCEPTOR_CODE_OK = 0;


    RouteBuilder route;

    Throwable e;

    String message;

    RouteInterceptorCallback callback;

    int code = INTERCEPTOR_CODE_OK;

    public RouteArgs(RouteBuilder route) {
        this.route = route;
    }
}
