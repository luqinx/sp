package chao.android.tools.service_pools.init;

import chao.java.tools.servicepool.IInitService;
import chao.java.tools.servicepool.Sp;
import chao.java.tools.servicepool.annotation.Init;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-09-17
 */
@Init(lazy = false)
@Service
public class TestLazyInitServicePriorityNormal implements IInitService {
    @Override
    public void onInit() {
        System.out.println("normal lazy service inited.");
    }
}
