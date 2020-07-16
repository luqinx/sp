package chao.android.tools.servicepool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import chao.android.tools.servicepool.route.RouteBuilder;
import chao.android.tools.servicepool.route.RouteManager;
import chao.java.tools.servicepool.ServicePool;
import dalvik.system.DexClassLoader;

/**
 * @author qinchao
 * @since 2019/6/19
 */
public class AndroidServicePool extends ServicePool {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;


    private static ClassLoader mockClassLoader;

    public static Context getContext() {
        return sContext;
    }

    public static void init(Context context) {
        sContext = context.getApplicationContext();
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                loadInitService();
            }
        });
        checkLoader();
        controller.cacheService(new RouteManager());
    }

    public static RouteBuilder build(String path) {
        return new RouteBuilder(path);
    }
}
