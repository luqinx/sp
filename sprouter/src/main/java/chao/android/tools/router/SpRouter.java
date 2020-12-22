package chao.android.tools.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;

import chao.android.tools.interceptor.Interceptor;
import chao.android.tools.servicepool.Spa;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.InnerProxy;
import chao.java.tools.servicepool.ServicePool;

/**
 * @author luqin
 * @since 2020-07-28
 */
public class SpRouter {

    static final String TAG = "SpRouter";

    public static final String ROUTER_KEY_PREFIX = "SpRouter##";

    public static final String ROUTE_PATH_KEY = ROUTER_KEY_PREFIX + "path";

    public static final String ROUTE_BUILD_PARAM = ROUTER_KEY_PREFIX + "RouteBuilderParam";

    static final Gson gson = new Gson();

    /**
     *  初始化
     */
    public static void init(Context context) {
        Spa.init(context);
    }

    public static <T extends IService> T getService(Class<T> serviceClass) {
        if (!serviceClass.isInterface()) {
            throw new IllegalArgumentException("serviceClass must be a interface class.");
        }
        T routerService = ServicePool.getService(serviceClass, null);
        if (routerService == null) {
            T service = Interceptor
                    .of(null, serviceClass)
                    .intercepted(true)
                    .interfaces(RouterService.class)
                    .newInstance();
            InnerProxy<T> innerProxy = new InnerProxy<>(service);
            innerProxy.setOriginClass(serviceClass);

            ServicePool.cacheService(serviceClass, innerProxy);
            routerService = innerProxy.getService();
        }
        return routerService;
    }

    public static <T> T getExtra(Activity activity, String key, Type type) {
        JsonElement element = getExtraInner(activity, key);
        if (element == null || element.isJsonNull()) {
            return null;
        }
        return gson.fromJson(element, type);
    }

    public static String getRoutePath(Activity activity) {
        if (activity == null) {
            return null;
        }
        Intent intent = activity.getIntent();
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(ROUTE_PATH_KEY);
    }


    private static JsonElement getExtraInner(Activity activity, String key) {
        Intent intent = activity.getIntent();
        if (intent == null) {
            return null;
        }
        String vs = intent.getStringExtra(ROUTER_KEY_PREFIX + key);
        if (vs == null) {
            return null;
        }
        return new JsonParser().parse(vs);
    }


    /**
     *  推荐使用{@link #getService} 的方式
     */
    public static RouteBuilder build(String path) {
        return new RouteBuilder(path);
    }

}
