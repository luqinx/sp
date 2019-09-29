package chao.android.tools.service_pools.init;

import chao.java.tools.servicepool.AbsInitServiceManager;

/**
 * @author luqin
 * @since 2019-09-17
 */
public class Test extends AbsInitServiceManager {

    public Test(){
        addInitService(TestInitService.class);
    }


}
