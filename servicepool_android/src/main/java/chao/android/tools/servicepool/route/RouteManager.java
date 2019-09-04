package chao.android.tools.servicepool.route;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2019-09-03
 */
public class RouteManager implements IService {

    private Map<String, Class<? extends Activity>> routeMap;

    public RouteManager() {
        routeMap = new HashMap<>();
    }

    public void addRoute(String path, Class<? extends Activity> activity) {
        if (!routeMap.containsKey(path)) {
            routeMap.put(path, activity);
        } else if (routeMap.get(path) != activity) {
            throw new IllegalArgumentException("duplicate route path, path already exists with value: " + routeMap.get(path)); //path不允许重复
        }
    }

    public Class<? extends Activity> getRoute(String path) {
        Class<? extends Activity> activity = routeMap.get(path);
        if (activity != null) {
            return activity;
        }
        return null;
    }

    public void navigation(Context context, RouteBuilder route) {
        Class<? extends Activity> activity = routeMap.get(route.path);
        if (activity == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtras(route.args);
        intent.setFlags(route.flags);
        intent.setClass(context, activity);
        if (context instanceof Activity) {
            if (route.exitAnim != -1 && route.enterAnim != -1) {
                ((Activity) context).overridePendingTransition(route.enterAnim, route.exitAnim);
            }
            ((Activity) context).startActivityForResult(intent, route.requestCode);
        } else {
            intent.setFlags(route.flags | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
