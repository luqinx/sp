package chao.android.tools.servicepool.route;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import chao.android.tools.servicepool.AndroidServicePool;
import chao.java.tools.servicepool.IPathService;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.annotation.Service;

import static chao.java.tools.servicepool.ServicePool.getCombineService;
import static chao.java.tools.servicepool.ServicePool.logger;

/**
 * @author luqin
 * @since 2019-09-03
 */
public class RouteManager implements IService {

    private static final String SUPPORT_FRAGMENT = "android.support.v4.app.Fragment";

    @Service
    private IPathService pathService;

    private static Handler mHandler;

    private RouteCombineStrategyImpl routeCombineStrategy;

    public RouteManager() {
        mHandler = new Handler(Looper.getMainLooper());

        routeCombineStrategy = new RouteCombineStrategyImpl();
    }

    Object navigation(final RouteBuilder route, final RouteNavigationCallback callback) {
        if (route.context == null) {
            route.context = AndroidServicePool.getContext();//Application Context
        }
        final Class<? extends IService> service = pathService.get(route.path);
        if (service == null) {
            if (callback != null) {
                callback.onLost(route);
            }
            logger.log("Router [%s] not found !!! ", route.path);
            return null;
        }

        if (Activity.class.isAssignableFrom(service)) {
            if (callback != null) {
                callback.onFound(service, route);
            }
            getCombineService(RouteInterceptor.class, routeCombineStrategy).intercept(route, new RouteInterceptorCallback() {
                @Override
                public void onContinue(final RouteBuilder route) {
                    runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            _navigationActivity(route, (Class<? extends Activity>) service, callback);
                        }
                    });
                }

                @Override
                public void onInterrupt(Throwable e) {
                    if (callback != null) {
                        callback.onInterrupt(route, e);
                    }
                    logger.log("Router Interceptor interrupted!!! " + e);
                }
            });

            return null;
        } else {
            if (callback != null) {
                callback.onLost(route);
            }
            logger.log("Router [%s] not found !!! ", route.path);
        }
        return null;
    }

    private void _navigationActivity(RouteBuilder route, Class<? extends Activity> activity, RouteNavigationCallback callback) {
        Intent intent = new Intent();
        intent.putExtras(route.extras);
        intent.setFlags(route.flags);
        if (route.type != null && route.uri != null) {
            intent.setDataAndType(route.uri, route.type);
        } else if (route.uri != null){
            intent.setData(route.uri);
        } else if (route.type != null) {
            intent.setType(route.type);
        }
        intent.setClass(route.context, activity);
        if (route.context instanceof Activity) {
            if (route.exitAnim != -1 || route.enterAnim != -1) {
                ((Activity) route.context).overridePendingTransition(route.enterAnim, route.exitAnim);
            }
            ((Activity) route.context).startActivityForResult(intent, route.requestCode);
        } else {
            intent.setFlags(route.flags | Intent.FLAG_ACTIVITY_NEW_TASK);
            route.context.startActivity(intent);
        }

        if (callback != null) {
            callback.onArrival(route);
        }
    }

    private void runInMainThread(Runnable runnable) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            mHandler.post(runnable);
        } else {
            runnable.run();
        }
    }
}
