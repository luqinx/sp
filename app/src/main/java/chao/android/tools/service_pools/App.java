package chao.android.tools.service_pools;

import android.app.Application;
import android.os.Debug;
import android.os.SystemClock;
import chao.android.tools.servicepool.AndroidServicePool;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class App extends Application {

    public App() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Debug.startMethodTracing("App.OnCreate." + SystemClock.elapsedRealtime(), 80 * 1024 * 1024);
        AndroidServicePool.init(this);

        System.out.println();

    }

}
