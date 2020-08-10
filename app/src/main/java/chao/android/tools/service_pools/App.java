package chao.android.tools.service_pools;

import android.app.Application;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import chao.android.tools.servicepool.SPA;
import chao.app.ami.Ami;
import leakcanary.LeakCanary;

/**
 * todo 问题:
 *  application使用service注解报错， context为空
 *
 *
 * @author qinchao
 * @since 2019/4/30
 */
public class App extends Application {

//    @Service
//    private Printer appService;
//
//    @Service(CommonPrinter.class)
//    private Printer commonService;
//
//    @Service(Haha.class)
//    private Printer haha;
//
////    @Service
////    private InitService5 initService5;
//
//    @Service
//    private TestPluginService testPluginService;
//
//
////    @Service
////    private InitService3 initService3;
//
//    @Service
//    private ILogger logger;

    public App() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        Stopwatch stopwatch = Stopwatch.createStarted();

        SPA.init(this);
        SPA.setExceptionHandler((e, message) -> {
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
