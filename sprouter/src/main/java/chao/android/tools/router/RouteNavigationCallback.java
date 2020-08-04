package chao.android.tools.router;

import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2019-09-30
 */
public interface RouteNavigationCallback {
    void onLost(RouteBuilder route);

    void onFound(Class<? extends IService> service, RouteBuilder builder);

    void onInterrupt(RouteBuilder route, Throwable e);

    void onArrival(RouteBuilder route);
}
