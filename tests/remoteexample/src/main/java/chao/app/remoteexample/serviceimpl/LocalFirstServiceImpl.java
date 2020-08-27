package chao.app.remoteexample.serviceimpl;

import chao.app.remoteapi.LocalFirstService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-07-27
 */
@Service
public class LocalFirstServiceImpl implements LocalFirstService {
    @Override
    public int getInt() {
        return 111;
    }

    @Override
    public String getString() {
        return "remote first";
    }

    @Override
    public void function() {
        System.out.println("remote local first called!");
    }
}
