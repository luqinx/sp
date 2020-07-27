package chao.app.remoteexample.service;

import chao.android.tools.servicepool.rpc.annotation.RemoteServiceConfig;
import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2020-07-27
 */
@RemoteServiceConfig(remotePackageName = "chao.app.remoteexample", remoteComponentName = "chao.app.remoteexample.ExampleService")
public interface LocalFirstService extends IService {
    int getInt();

    String getString();

    void function();

}
