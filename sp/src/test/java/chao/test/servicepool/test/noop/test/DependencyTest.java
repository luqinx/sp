package chao.test.servicepool.test.noop.test;

import chao.java.tools.servicepool.ServicePool;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class DependencyTest {

    @Before
    public void init() {
        ServicePool.loadServices();
    }

    @Test
    public void testDependency() {

    }
}
