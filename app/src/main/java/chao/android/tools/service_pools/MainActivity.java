package chao.android.tools.service_pools;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.example.testpluginlib.TestPluginService;

import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author qinchao
 * @since 2019/4/29
 */
public class MainActivity extends AppCompatActivity {

    @Service
    private Printer appService;

    @Service
    private TestPluginService testPluginService;

    {
//        if (Printer.class.isAssignableFrom(AppService.class)) {
//            appService = ServicePool.getService(AppService.class);
//        }
//        appService = ServicePool.getService(AppService.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppLibService appLibService = ServicePool.getService(AppLibService.class);
//        appLibService.appPrint();

//        testPluginService = ServicePool.getService(TestPluginService.class);
        testPluginService.print();



//        TestPluginService service = new TestPluginService();
//        Log.e("System.out", String.valueOf(service instanceof IService));

//        appService = ServicePool.getService(AppService.class);
        appService.print();

    }
}
