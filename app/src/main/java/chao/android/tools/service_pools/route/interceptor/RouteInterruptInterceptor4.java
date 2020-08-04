package chao.android.tools.service_pools.route.interceptor;

import chao.android.tools.router.RouteBuilder;
import chao.android.tools.router.RouteInterceptor;
import chao.android.tools.router.RouteInterceptorCallback;
import chao.app.ami.Ami;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-10-09
 */
@Service(priority = 4, scope = IService.Scope.global)
public class RouteInterruptInterceptor4 implements RouteInterceptor {
    @Override
    public void intercept(RouteBuilder route, RouteInterceptorCallback callback) {
        Ami.log(route);
        if (route.extras.getInt("interceptor") == 1) {
            callback.onInterrupt(null);
        } else {
            callback.onContinue(route);
        }
    }
}
