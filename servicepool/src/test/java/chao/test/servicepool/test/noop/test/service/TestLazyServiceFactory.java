package chao.test.servicepool.test.noop.test.service;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactory;
import chao.java.tools.servicepool.ServiceProxy;

import java.util.HashSet;

/**
 * @author qinchao
 * @since 2019/5/4
 */
public class TestLazyServiceFactory implements IServiceFactory {

    @Override
    public HashSet<ServiceProxy> createServiceProxies(Class<?> clazz) {
        return null;
    }

    @Override
    public ServiceProxy createServiceProxy(Class<?> clazz) {
        return null;
    }

    @Override
    public IService createInstance(Class<?> clazz) {
        return null;
    }
}
