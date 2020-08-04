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
@Service(priority = 1,scope = IService.Scope.global)
public class RouteExceptionInterceptor1 implements RouteInterceptor {
    @Override
    public void intercept(RouteBuilder route, RouteInterceptorCallback callback) {
        Ami.log(route);
        if (route.extras.getInt("interceptor") == 2) {
            throw new RuntimeException("route interceptor err test.");
        } else {
            callback.onContinue(route);
        }
    }
}
