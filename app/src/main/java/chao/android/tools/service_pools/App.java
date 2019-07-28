package chao.android.tools.service_pools;

import android.app.Application;
import android.os.Bundle;
import android.os.SystemClock;
import chao.android.tools.servicepool.AndroidServicePool;
import chao.android.tools.servicepool.route.Route;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class App extends Application {

    @Service(tag="/activity/1")
    private Route route;

    public App() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidServicePool.init(this);
    }

}
