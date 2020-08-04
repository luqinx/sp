package chao.android.tools.service_pools.rpc;

import chao.android.tools.service_pools.SimpleFragment;
import chao.android.tools.servicepool.rpc.RemoteCallbackHandler;
import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2020-08-03
 */
public interface TestService extends IService {
    void test(RemoteCallbackHandler<SimpleFragment.SimpleData> callbackHandler);
}
