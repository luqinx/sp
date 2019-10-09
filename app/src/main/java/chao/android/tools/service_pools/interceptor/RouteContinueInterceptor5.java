package chao.android.tools.service_pools.interceptor;

import chao.android.tools.servicepool.route.RouteBuilder;
import chao.android.tools.servicepool.route.RouteInterceptor;
import chao.android.tools.servicepool.route.RouteInterceptorCallback;
import chao.app.ami.Ami;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-10-09
 */
@Service(priority = 5, scope = IService.Scope.global)
public class RouteContinueInterceptor5 implements RouteInterceptor {
    @Override
    public void intercept(RouteBuilder route, RouteInterceptorCallback callback) {
        Ami.log(route);
        callback.onContinue(route);
    }
}
