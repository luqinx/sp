package chao.test.servicepool.test.noop.test;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.NoOpInstance;
import chao.java.tools.servicepool.ServicePool;
import chao.test.servicepool.test.noop.test.service.II;
import chao.test.servicepool.test.noop.test.service.TestAbsImplService;
import chao.test.servicepool.test.noop.test.service.TestAbsService;
import chao.test.servicepool.test.noop.test.service.TestService;
import chao.test.servicepool.test.noop.test.service.TestService2;
import chao.test.servicepool.test.noop.test.service.TestWeakService;
import com.android.tools.testsuit.internal.SampleCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class ServiceTest extends SampleCase {

    private int weakCode = 0;

    @Before
    public void init() {
        weakCode = ServicePool.getService(TestWeakService.class).hashCode();
    }

    @Test
    public void testService() {
        assertInstanceOf(ServicePool.getService(TestService.class), TestService.class);
        assertNotInstanceOf(ServicePool.getService(TestService.class), NoOpInstance.class);

        assertInstanceOf(ServicePool.getService(TestService2.class), TestService2.class);
        assertNotInstanceOf(ServicePool.getService(TestService2.class), NoOpInstance.class);

    }

    @Test
    public void testWeakService() {
        assertEquals(weakCode, ServicePool.getService(TestWeakService.class).hashCode());
        System.gc();
        assertNotEquals(weakCode, ServicePool.getService(TestWeakService.class).hashCode());
    }

    @Test
    public void testNoPublicServcie() throws ClassNotFoundException {
        ServicePool.getService(Class.forName("chao.test.servicepool.test.noop.test.service.NoPublicService").asSubclass(IService.class));
    }

    @Test
    public void testAbsService() {
        assertInstanceOf(ServicePool.getService(TestAbsService.class, TestAbsImplService.class), TestAbsImplService.class);
        assertNotInstanceOf(ServicePool.getService(TestAbsService.class, TestAbsImplService.class), NoOpInstance.class);
    }

    @Test
    public void testInterfaceService() {
        assertInstanceOf(ServicePool.getService(II.class, TestAbsImplService.class), TestAbsImplService.class);
        assertNotInstanceOf(ServicePool.getService(II.class, TestAbsImplService.class), NoOpInstance.class);
    }
}
