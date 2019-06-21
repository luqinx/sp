package chao.test.servicepool.test.noop;

import chao.java.tools.servicepool.NoOpInstanceFactory;

/**
 * @author qinchao
 * @since 2019/5/5
 */
public class NoOpInstanceFactoryForTest extends NoOpInstanceFactory {

    @Override
    public <T> T newInstance(Class<T> clazz) {
        return super.newInstance(clazz);
    }
}
