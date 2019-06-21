package chao.test.servicepool.test.noop.test.service;


import chao.java.tools.servicepool.DependencyManager;
import chao.java.tools.servicepool.IInitService;

/**
 * @author qinchao
 * @since 2019/5/5
 */
public class TestDependencyManager implements DependencyManager {
    @Override
    public void addService(IInitService service) {

    }

    @Override
    public void servicesInit() {

    }
}
