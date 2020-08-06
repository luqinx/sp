package chao.app.remoteexample.serviceimpl;

import java.util.List;

import chao.android.tools.rpc.RemoteCallbackHandler;
import chao.app.remoteapi.IExampleService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-07-23
 */
@Service
public class ExampleServiceImpl implements IExampleService {
    @Override
    public int getInt() {
        return 50;
    }

    @Override
    public String getString() {
        return "hi, luqin";
    }

    @Override
    public void function() {
        System.out.println("function called");
    }

    @Override
    public int withInt(int i) {
        return i;
    }

    @Override
    public int withII(int i1, int i2) {
        return i1 + i2;
    }

    @Override
    public void withList(int i, String s, List<String> sl) {
    }

    @Override
    public void withCallback(int i, RemoteCallbackHandler<String> handler) {
        handler.resolve("with callback method: " + i);
    }

    @Override
    public void withString(String s) {

    }
}
