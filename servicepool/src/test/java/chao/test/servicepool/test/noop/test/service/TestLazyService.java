package chao.test.servicepool.test.noop.test.service;

import chao.java.tools.servicepool.IService;

/**
 * @author qinchao
 * @since 2019/5/4
 */
public class TestLazyService implements IService {
    @Override
    public String tag() {
        return null;
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public int scope() {
        return 0;
    }
}
