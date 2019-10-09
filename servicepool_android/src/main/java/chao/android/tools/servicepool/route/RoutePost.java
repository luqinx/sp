package chao.android.tools.servicepool.route;

/**
 * @author luqin
 * @since 2019-10-09
 */
public class RoutePost {

    static final int INTERCEPTOR_CODE_USER = -2;

    static final int INTERCEPTOR_CODE_ERR = -1;

    static final int INTERCEPTOR_CODE_OK = 0;


    RouteBuilder route;

    Throwable e;

    String message;

    int code = INTERCEPTOR_CODE_OK;

    public RoutePost(RouteBuilder route) {
        this.route = route;
    }

    public RouteBuilder getRoute() {
        return route;
    }

    public void setRoute(RouteBuilder route) {
        this.route = route;
    }

    public Throwable getE() {
        return e;
    }

    public void setE(Throwable e) {
        this.e = e;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
