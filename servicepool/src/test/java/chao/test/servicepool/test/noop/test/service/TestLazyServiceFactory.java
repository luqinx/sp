package chao.test.servicepool.test.noop.test.service;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactory;
import java.util.Arrays;

/**
 * @author qinchao
 * @since 2019/5/4
 */
public class TestLazyServiceFactory implements IServiceFactory {

    @Override
    public Iterable<Class<? extends IService>> createServices() {
        return Arrays.asList(TestLazyService1.class, TestLazyService2.class, TestWeakService.class);
//        return Collections.emptyList();
    }
}
