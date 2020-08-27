package chao.app.remoteapi;

import chao.android.tools.rpc.annotation.RemoteServiceConfig;

/**
 * @author luqin
 * @since 2020-07-23
 */
@RemoteServiceConfig(remotePackageName = "chao.app.remoteexample",
        remoteComponentName = "chao.app.remoteexample.ExampleService",
        forceMainThread = true,
        timeout = 3001
)
public interface RPCForceMainService extends IExampleService {
}
