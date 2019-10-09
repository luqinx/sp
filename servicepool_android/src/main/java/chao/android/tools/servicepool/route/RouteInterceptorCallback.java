package chao.android.tools.servicepool.route;

/**
 * @author luqin
 * @since 2019-09-30
 */
public interface RouteInterceptorCallback {
    void onContinue(RouteBuilder route);

    void onInterrupt(Throwable e);
}
