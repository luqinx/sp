package chao.android.tools.service_pools;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import chao.android.tools.router.SpRouter;
import chao.android.tools.service_pools.abs.Abs;
import chao.android.tools.service_pools.event.EventSample;
import chao.android.tools.service_pools.event.HisEvent;
import chao.android.tools.service_pools.event.MyEvent;
import chao.android.tools.service_pools.fragments.EventFragment;
import chao.android.tools.service_pools.fragments.HisEventFragment;
import chao.android.tools.service_pools.init.InitSampleActivity;
import chao.android.tools.service_pools.init.InitSyncSampleFragment;
import chao.android.tools.service_pools.path.PathService;
import chao.android.tools.service_pools.path.PathService2;
import chao.android.tools.service_pools.path.RepeatePathService;
import chao.android.tools.service_pools.route.interceptor.RouteContinueInterceptor5;
import chao.android.tools.service_pools.rpc.RPCSampleFragment;
import chao.android.tools.service_pools.test.Haha;
import chao.android.tools.service_pools.xxxxx.ASMStaticClass;
import chao.android.tools.router.RouteInterceptor;
import chao.app.ami.Ami;
import chao.app.ami.UI;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.NoOpInstance;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.annotation.Event;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author qinchao
 * @since 2019/4/29
 */
public class MainActivity extends AppCompatActivity implements HisEvent {

    private static final Class[] classes = {
            Printer.class, CommonPrinter.class, Haha.class, A.class, SecondActivity.SecondPrinter.class,
            AppService.class, AppService2.class, InnerService.class, PathService.class, Abs.class
    };

    @Service
    private Printer appService;

    @Service(CommonPrinter.class)
    private Printer commonService;

    @Service(Haha.class)
    private Printer haha;

    EventSample eventSample = new EventSample();


    @Service
    private A a;

//    @Service
//    private TestPluginService testPluginService;

    @Service(SecondActivity.SecondPrinter.class)
    private Printer main;

//    @Service
//    private static Printer sPrinter;

//    @Service
//    private InitService5 initService5;

    @Service
    private AppService2 appService2;

    @Service
    private InnerService innerService;

    @Event
    private MyEvent myEvent;

    @Service(path = "/app/path")
    private PathService pathService;

    @Service(path = "/app/path2")
    private static PathService2 pathService2;

    @Service
    private Abs abs;

    public MainActivity() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (Class clazz: classes) {
//            ServicePool.getService(clazz);
        }
        Ami.log(stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


//        testPluginService = ServicePool.getService(TestPluginService.class);
//        testPluginService.print();

        IService iService = ServicePool.getService("/app/path2");
        System.out.println("iService instance of PathService2:" + (iService instanceof PathService2));


        pathService.print();

        pathService2.print();

//
//        abs.method();

//        System.out.println("abs instance of NoInstance:" + (abs instanceof NoOpInstance));
//
        Printer commonService = ServicePool.getService(CommonPrinter.class);
        commonService.print();
//
        System.out.println(commonService);
//
//        TestPluginService service = new TestPluginService();
//        Log.e("System.out", String.valueOf(service instanceof IService));
//
////        appService = ServicePool.getService(AppService.class);
        appService.print();
//
        haha.print();
        a.getInt();

        main.print();


        RouteInterceptor global1 = ServicePool.getService(RouteContinueInterceptor5.class);
        RouteInterceptor global2 = ServicePool.getService(RouteInterceptor.class);
        if (ServicePool.getFixedService(RouteContinueInterceptor5.class) != ServicePool.getService(RouteInterceptor.class)) {
            System.out.println("global 1: " + global1);
            System.out.println("global 2:" + global2);
            throw new RuntimeException();
        }


        //测试Scope为global
        if (ServicePool.getService("/app/path") != ServicePool.getService("/app/path")) {
            throw new RuntimeException();
        }

        //测试Scope为global, 通过path拿到的service和通过class拿到的service是同一个对象
        if (ServicePool.getService("/app/path") != ServicePool.getService(PathService.class)) {
            throw new RuntimeException();
        }

        if (ServicePool.getService(RepeatePathService.class) != ServicePool.getService("/app/repeatable")) {
            throw new RuntimeException();
        }

        //测试Scope为once
        if (ServicePool.getService("/app/path2") == ServicePool.getService("/app/path2")) {
            throw new RuntimeException();
        }

        findViewById(R.id.btn).setOnClickListener(v->{
            UI.show(this, EventFragment.class);
        });

        findViewById(R.id.his_btn).setOnClickListener(v -> {
            UI.show(this, HisEventFragment.class);
        });

        findViewById(R.id.init).setOnClickListener(v -> {
            UI.show(this, InitSampleActivity.class);
        });

        findViewById(R.id.rpc).setOnClickListener(v -> {
            UI.show(this, RPCSampleFragment.class);
        });

        new ASMStaticClass().printer();

        appService2.print();

        //todo android 4.4及以下版本设备会报空
//        innerService.print();

        findViewById(R.id.router).setOnClickListener(v -> {
            SpRouter.build("/app/testRoute")
                    .withContext(this)
                    .withInt("int", 100)
                    .withBoolean("boolean", true)
                    .withFloat("float", 100.1f)
                    .withDouble("double", 100.2)
                    .withString("string", "hello luqin")
                    .withSerializable("", new Integer[5])
//                .withSerializable("parcelable")
                    .navigation(null);
        });

        findViewById(R.id.init_sync).setOnClickListener(v -> {
            UI.show(this, InitSyncSampleFragment.class);
        });


        Printer repeat = ServicePool.getService("/app/repeat");
        repeat.print();

        Printer repeatable = ServicePool.getService("/app/repeatable");
        repeatable.print();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println();
    }

    @Override
    public void postHisEvent() {
        System.out.println("post his event in MainActivity .");
    }

    public class InnerEvent implements MyEvent {

        @Override
        public void postEvent() {
            System.out.println("post event in MainActivity inner.");
        }
    }

    /**
     * android 4.4及以下会报异常
     */
    @Service
    public class InnerService implements Printer {

        @Override
        public void print() {
            System.out.println("I'm inner service.");
        }
    }
}
