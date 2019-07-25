package chao.android.tools.service_pools;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.testpluginlib.TestPluginService;

import chao.android.tools.service_pools.test.Haha;
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

    @Service
    private TestPluginService testPluginService;

    {
//        appService = ServicePool.getService(AppService.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppLibService appLibService = ServicePool.getService(AppLibService.class);
//        appLibService.appPrint();

//        testPluginService = ServicePool.getService(TestPluginService.class);
        testPluginService.print();


        commonService.print();

//        TestPluginService service = new TestPluginService();
//        Log.e("System.out", String.valueOf(service instanceof IService));

//        appService = ServicePool.getService(AppService.class);
        appService.print();

        haha.print();

    }
}
