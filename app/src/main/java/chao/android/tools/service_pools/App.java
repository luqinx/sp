package chao.android.tools.service_pools;

import android.app.Application;

import com.example.testpluginlib.TestPluginService;

import chao.android.tools.service_pools.test.Haha;
import chao.android.tools.service_pools.test.InitService3;
import chao.android.tools.servicepool.AndroidServicePool;
import chao.java.tools.servicepool.ILogger;
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

    @Service
    private Printer appService;

    @Service(CommonPrinter.class)
    private Printer commonService;

    @Service(Haha.class)
    private Printer haha;

//    @Service
//    private InitService5 initService5;

    @Service
    private TestPluginService testPluginService;


    @Service
    private InitService3 initService3;

    @Service
    private ILogger logger;

    public App() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidServicePool.init(this);
        AndroidServicePool.setExceptionHandler((e, service) -> {
            logger.log(service);
        });

        commonService.print();

        haha.print();

        appService.print();
    }
}
