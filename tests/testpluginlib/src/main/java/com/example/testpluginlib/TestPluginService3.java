package com.example.testpluginlib;

import chao.java.tools.servicepool.DefaultService;
import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since  2019-07-09
 */
public class TestPluginService3 extends DefaultService implements IService {

    @Override
    public String path() {
        return "path";
    }

    @Override
    public int priority() {
        return Integer.valueOf(100);
    }

    @Override
    public int scope() {
        return Scope.once;
    }
}
