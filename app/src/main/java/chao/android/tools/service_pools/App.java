package chao.android.tools.service_pools;

import android.app.Application;
import android.os.Bundle;
import android.os.SystemClock;

import chao.android.tools.service_pools.test.InitService3;
import chao.android.tools.servicepool.AndroidServicePool;
import chao.android.tools.servicepool.route.Route;
import chao.java.tools.servicepool.annotation.Service;

/**
 * todo 问题:
 *  application使用service注解报错， context为空
 *
 *
 * @author qinchao
 * @since 2019/4/30
 */
public class App extends Application {

//    @Service(tag="/activity/1")
//    private Route route;

//    @Service
//    private InitService3 initService3;

    public App() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidServicePool.init(this);


    }

}
