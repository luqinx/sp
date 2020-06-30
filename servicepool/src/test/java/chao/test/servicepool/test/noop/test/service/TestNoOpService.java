package chao.test.servicepool.test.noop.test.service;

import chao.java.tools.servicepool.IService;

/**
 * @author qinchao
 * @since 2019/5/5
 */
public class TestNoOpService implements NoOp, IService {

    public Integer getInteger() {
        return 10086;
    }
}
