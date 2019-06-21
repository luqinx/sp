package chao.test.servicepool.test.noop.test;

import chao.java.tools.servicepool.NoOpInstance;
import chao.test.servicepool.test.noop.NoOpInstanceFactoryForTest;
import chao.test.servicepool.test.noop.TestNoOpObject;
import chao.test.servicepool.test.noop.test.noop.TestNoOpObjectWithArgumentsConstructor;
import chao.test.servicepool.test.noop.test.noop.TestNoOpObjectWithDefaultConstructor;
import chao.test.servicepool.test.noop.test.noop.TestNoOpObjectWithProtectedConstructor;
import com.android.tools.testsuit.internal.SampleCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qinchao
 * @since 2019/5/5
 */
public class TestOpInstanceFactory extends SampleCase {

    private NoOpInstanceFactoryForTest factory;

    @Before
    public void init() {
        factory = new NoOpInstanceFactoryForTest();
    }

    @Test
    public void testNewInstance() {
        assertInstanceOf(factory.newInstance(TestNoOpObject.class), NoOpInstance.class);

        assertInstanceOf(factory.newInstance(TestNoOpObjectWithDefaultConstructor.class), NoOpInstance.class);

        assertInstanceOf(factory.newInstance(TestNoOpObjectWithProtectedConstructor.class), NoOpInstance.class);

        assertInstanceOf(factory.newInstance(TestNoOpObjectWithArgumentsConstructor.class), NoOpInstance.class);

//        assertInstanceOf(factory.newInstance(TestNoOpObjectWithPrivateConstructor.class), NoOpInstance.class);

    }
}
