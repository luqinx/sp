package chao.test.servicepool.test.noop.test.service;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactory;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class ServiceFactory implements IServiceFactory {

    @Override
    public Iterable<Class<? extends IService>> createServices() {
        return null;
    }
}
