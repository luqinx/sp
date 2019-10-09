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
@Service(priority = 1,scope = IService.Scope.global)
public class RouteExceptionInterceptor1 implements RouteInterceptor {
    @Override
    public void intercept(RouteBuilder route, RouteInterceptorCallback callback) {
        Ami.log(route);
        throw new RuntimeException("route interceptor err test.");
    }
}
