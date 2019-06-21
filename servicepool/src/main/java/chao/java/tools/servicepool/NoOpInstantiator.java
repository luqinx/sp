package chao.java.tools.servicepool;

import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qinchao
 * @since 2019/6/19
 */
public interface NoOpInstantiator extends IService{
    <T> Class<?> make(Class<T> clazz, Constructor<?> constructor, Object[] params, AtomicInteger noOpCount);
}
