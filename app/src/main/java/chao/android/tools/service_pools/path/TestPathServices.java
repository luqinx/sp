package chao.android.tools.service_pools.path;

import chao.java.tools.servicepool.PathServices;

/**
 * @author luqin
 * @since 2019-09-18
 */
public class TestPathServices extends PathServices {

    public TestPathServices() {
        put("/app/path", PathService.class);
    }
}
