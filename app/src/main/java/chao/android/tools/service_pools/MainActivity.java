package chao.android.tools.service_pools;

import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import chao.java.tools.servicepool.ServicePool;
import chao.test.applib1.AppLibService;

/**
 * @author qinchao
 * @since 2019/4/29
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLibService appLibService = ServicePool.getService(AppLibService.class);
        appLibService.appPrint();

        Debug.stopMethodTracing();
    }
}
