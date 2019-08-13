package chao.android.tools.service_pools.test;

import android.os.SystemClock;

import chao.java.tools.servicepool.IInitService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-08-06
 */
@Service
public class InitService1 implements IInitService {
    @Override
    public void onInit() {
        SystemClock.sleep(1000);
    }
}
