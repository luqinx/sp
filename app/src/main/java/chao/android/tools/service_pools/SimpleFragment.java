package chao.android.tools.service_pools;

import android.view.View;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chao.android.tools.service_pools.xxxxx.ASMServiceProxy;
import chao.android.tools.servicepool.Spa;
import chao.app.ami.base.AmiSimpleFragment;

/**
 * @author luqin
 * @since 2020-07-02
 */
public class SimpleFragment extends AmiSimpleFragment {


    @Override
    public void onClick(View v) {
        ExecutorService executorService = Executors.newFixedThreadPool(300);
        for (int i = 0; i < 300; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Spa.init(App.sContext);
                }
            });
        }
    }

}
