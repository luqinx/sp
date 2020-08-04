package chao.app.remoteexample.serviceimpl;

import java.util.List;

import chao.android.tools.servicepool.rpc.RemoteCallbackHandler;
import chao.app.remoteapi.RPCForceMainService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-07-27
 */
@Service
public class RPCForceMainServiceImpl implements RPCForceMainService {
    @Override
    public int getInt() {
        return 60;
    }

    @Override
    public String getString() {
        return "rpc remote force main thread";
    }

    @Override
    public void function() {

    }

    @Override
    public int withInt(int i) {
        return 10;
    }

    @Override
    public int withII(int i1, int i2) {
        return 10;
    }

    @Override
    public void withList(int i, String s, List<String> sl){
    }

    @Override
    public void withCallback(int i, RemoteCallbackHandler<String> handler) {
        handler.resolve("(force main) with callback method: " + i);
    }

    @Override
    public void withString(String s) {

    }
}
