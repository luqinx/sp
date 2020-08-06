package chao.android.tools.service_pools.rpc;

import chao.android.tools.rpc.RemoteCallbackHandler;
import chao.android.tools.service_pools.router2.RouteFragment;
import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2020-08-03
 */
public interface TestService extends IService {
    void test(RemoteCallbackHandler<RouteFragment.SimpleData> callbackHandler);
}
