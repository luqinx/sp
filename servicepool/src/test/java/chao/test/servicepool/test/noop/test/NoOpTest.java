package chao.test.servicepool.test.noop.test;

import chao.java.tools.servicepool.NoOpInstance;
import chao.java.tools.servicepool.ServicePool;
import chao.test.servicepool.test.noop.test.service.NoOp;
import chao.test.servicepool.test.noop.test.service.TestNoOpService;
import com.android.tools.testsuit.internal.SampleCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qinchao
 * @since 2019/5/1
 */
public class NoOpTest extends SampleCase {

    @Before
    public void init() {
    }

    @Test
    public void testNoOpClass() {
        assertInstanceOf(ServicePool.getService(TestNoOpService.class), NoOpInstance.class);
        System.out.println(ServicePool.getService(TestNoOpService.class));

        assertEquals(ServicePool.getService(TestNoOpService.class).hashCode(), 0);
        assertEquals(ServicePool.getService(TestNoOpService.class).getInteger(), new Integer(0));
        assertFalse(ServicePool.getService(TestNoOpService.class).equals(ServicePool.getService(TestNoOpService.class)));
    }

    @Test
    public void testNoOpInterface() {

        assertEquals(ServicePool.getService(TestNoOpService.class).hashCode(), 0);
        assertEquals(ServicePool.getService(TestNoOpService.class).getInteger(), new Integer(0));
        assertFalse(ServicePool.getService(TestNoOpService.class).equals(ServicePool.getService(TestNoOpService.class)));

    }
}
