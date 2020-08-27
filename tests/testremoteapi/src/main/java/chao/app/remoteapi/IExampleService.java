package chao.app.remoteapi;

import java.util.List;

import chao.android.tools.rpc.RemoteCallbackHandler;
import chao.android.tools.rpc.annotation.RemoteServiceConfig;
import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2020-07-23
 */
@RemoteServiceConfig(remotePackageName = "chao.app.remoteexample",
        remoteComponentName = "chao.app.remoteexample.ExampleService",
        timeout = 50000000
)
public interface IExampleService extends IService {
    int getInt();

    String getString();

    void function();

    int withInt(int i);

    int withII(int i1, int i2);

    void withString(String s);

    void withList(int i, String s, List<String> sl);

    void withCallback(int i, RemoteCallbackHandler<String> handler);

}
