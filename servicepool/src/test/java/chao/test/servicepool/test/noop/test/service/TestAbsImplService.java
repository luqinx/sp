package chao.test.servicepool.test.noop.test.service;

import chao.java.tools.servicepool.IService;

/**
 * @author qinchao
 * @since 2019/5/5
 */
public class TestAbsImplService extends TestAbsService implements IService, II {
    @Override
    public String tag() {
        return null;
    }
}
