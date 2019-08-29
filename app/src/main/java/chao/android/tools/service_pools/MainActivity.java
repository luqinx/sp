package chao.android.tools.service_pools;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import chao.android.tools.service_pools.event.EventSample;
import chao.android.tools.service_pools.event.MyEvent;
import chao.android.tools.service_pools.fragments.EventFragment;
import chao.android.tools.service_pools.test.Haha;
import chao.android.tools.service_pools.test.InitService5;
import chao.android.tools.service_pools.xxxxx.ASMStaticClass;
import chao.app.ami.UI;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author qinchao
 * @since 2019/4/29
 */
public class MainActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        AppLibService appLibService = ServicePool.getService(AppLibService.class);
//        appLibService.appPrint();

//        testPluginService = ServicePool.getService(TestPluginService.class);
//        testPluginService.print();

//
        ServicePool.registerEventService(new InnerEvent());

//
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

        findViewById(R.id.btn).setOnClickListener(v->{
            UI.show(this, EventFragment.class);
        });

        new ASMStaticClass().printer();

        appService2.print();

        innerService.print();

    }

    public class InnerEvent implements MyEvent {

        @Override
        public void postEvent() {
            System.out.println("post event in MainActivity inner.");
        }
    }

    @Service
    private class InnerService implements Printer {

        @Override
        public void print() {
            System.out.println("I'm inner service.");
        }
    }
}
