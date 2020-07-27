package chao.app.remoteexample.service.impl;

import chao.app.remoteexample.service.LocalFirstService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-07-27
 */
@Service
public class LocalFirstServiceImpl implements LocalFirstService {
    @Override
    public int getInt() {
        return 55;
    }

    @Override
    public String getString() {
        return "local first";
    }

    @Override
    public void function() {
        System.out.println("local first called!");
    }
}
