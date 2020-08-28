package chao.android.tools.service_pools;

import android.app.Application;
import android.content.Context;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import chao.android.tools.servicepool.Spa;
import chao.app.ami.Ami;

/**
 *
 *
 * @author qinchao
 * @since 2019/4/30
 */
public class App extends Application {

    public static Context sContext;

    private static Application sApp;

    public App() {
    }

    public static Context getContext() {
        return sContext;
    }

    public static Application application() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sApp = this;

        sContext = getApplicationContext();

        Stopwatch stopwatch = Stopwatch.createStarted();


//        new SimpleFragment().onClick(null);

        Spa.init(this);
        Spa.setExceptionHandler((e, message) -> {
//            logger.log(message);
            e.printStackTrace();
            System.out.println("get service err: " + e);
        });

        Ami.log(stopwatch.elapsed(TimeUnit.MILLISECONDS));

//        Ami.init(this);
//        Ami.setDrawerId(R.raw.ami_config);
        Ami.log(stopwatch.elapsed(TimeUnit.MILLISECONDS));

//        commonService.print();
//
//        haha.print();
//
//        appService.print();
    }
}
