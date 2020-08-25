package chao.android.tools.service_pools.test;

import android.os.SystemClock;

import chao.app.ami.Ami;
import chao.java.tools.servicepool.IInitService;
import chao.java.tools.servicepool.Sp;
import chao.java.tools.servicepool.annotation.Init;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-08-06
 */
@Init(dependencies = InitService1.class, priority = Sp.MAX_PRIORITY)
@Service
public class InitService2 implements IInitService {
    @Override
    public void onInit() {
        Ami.log("start");

        SystemClock.sleep(1000);
        Ami.log("done");

    }
}
