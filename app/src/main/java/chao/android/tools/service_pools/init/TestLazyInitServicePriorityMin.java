package chao.android.tools.service_pools.init;

import chao.java.tools.servicepool.IInitService;
import chao.java.tools.servicepool.Sp;
import chao.java.tools.servicepool.annotation.Init;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-09-17
 */
@Init(lazy = false, priority = Sp.MIN_PRIORITY)
@Service
public class TestLazyInitServicePriorityMin implements IInitService {
    @Override
    public void onInit() {
        System.out.println("min priority lazy service inited.");
    }
}
