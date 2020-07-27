package chao.app.remoteexample.service;

import chao.android.tools.servicepool.rpc.annotation.RemoteServiceConfig;

/**
 * @author luqin
 * @since 2020-07-23
 */
@RemoteServiceConfig(remotePackageName = "chao.app.remoteexample", remoteComponentName = "chao.app.remoteexample.ExampleService", forceMainThread = true)
public interface RPCForceMainService extends IExampleService {
}
