package com.example.testpluginlib;

import chao.java.tools.servicepool.DefaultService;
import chao.java.tools.servicepool.IService;

/**
 * @author luqin  qinchao@mochongsoft.com
 * @project: zmjx-sp
 * @description:
 * @date 2019-07-09
 */
public class TestPluginService3 extends DefaultService implements IService {

    @Override
    public String tag() {
        return "tag";
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
