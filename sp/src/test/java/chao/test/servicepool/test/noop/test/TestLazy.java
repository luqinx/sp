package chao.test.servicepool.test.noop.test;

import chao.java.tools.servicepool.NoOpInstance;
import chao.java.tools.servicepool.ServicePool;
import chao.test.servicepool.test.noop.test.service.TestLazyService;
import chao.test.servicepool.test.noop.test.service.TestLazyService1;
import chao.test.servicepool.test.noop.test.service.TestLazyService2;
import com.android.tools.testsuit.internal.SampleCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qinchao
 * @since 2019/5/4
 */
public class TestLazy extends SampleCase {

    @Before
    public void init() {
        ServicePool.loadServices();
    }

    @Test
    public void testGetLazyService() {
        assertNotInstanceOf(ServicePool.getService(TestLazyService.class), NoOpInstance.class);

        assertNotInstanceOf(ServicePool.getService(TestLazyService1.class), NoOpInstance.class);

        assertNotInstanceOf(ServicePool.getService(TestLazyService2.class), NoOpInstance.class);
    }
}
