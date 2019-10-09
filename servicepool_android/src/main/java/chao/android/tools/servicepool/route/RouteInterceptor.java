package chao.android.tools.servicepool.route;

import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2019-10-08
 */
public interface RouteInterceptor extends IService {
    void intercept(RouteBuilder route, RouteInterceptorCallback callback);
}
