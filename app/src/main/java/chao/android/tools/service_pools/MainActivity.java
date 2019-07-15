package chao.android.tools.service_pools;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.testpluginlib.TestPluginService;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.annotation.Auto;
import chao.java.tools.servicepool.gen_test.MyService1;
import chao.test.applib1.AppLibService;

/**
 * @author qinchao
 * @since 2019/4/29
 */
public class MainActivity extends AppCompatActivity {

    @Auto
    private AppService appService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLibService appLibService = ServicePool.getService(AppLibService.class);
        appLibService.appPrint();

        TestPluginService pluginService = ServicePool.getService(TestPluginService.class);
        pluginService.print();

        MyService1 myService1 = ServicePool.getService(MyService1.class);
        myService1.print();


        TestPluginService service = new TestPluginService();
        Log.e("qinchao", String.valueOf(service instanceof IService));

//        AppService appService = ServicePool.getService(AppService.class);
        appService.print();

    }
}
