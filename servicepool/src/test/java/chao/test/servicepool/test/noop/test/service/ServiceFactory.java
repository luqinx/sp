package chao.test.servicepool.test.noop.test.service;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactory;
import chao.java.tools.servicepool.ServiceProxy;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class ServiceFactory implements IServiceFactory {

    @Override
    public ServiceProxy createServiceProxy(Class<?> clazz) {
        return null;
    }

    @Override
    public IService createInstance(Class<?> clazz) {
        return null;
    }
}
