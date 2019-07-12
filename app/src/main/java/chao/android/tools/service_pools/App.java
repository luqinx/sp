package chao.android.tools.service_pools;

import android.app.Application;
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
        AndroidServicePool.init(this);
    }

}
