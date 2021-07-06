package chao.java.tools.servicepool;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qinchao
 * @since 2019/6/19
 */
public class DefaultNoOpInstantiator extends DefaultService implements NoOpInstantiator {
    @Override
    public <T> Class<?> make(Class<T> clazz, Constructor<?> constructor, Object[] params, AtomicInteger noOpCount) {
        return new ByteBuddy()
            .subclass(clazz, ConstructorStrategy.Default.NO_CONSTRUCTORS)
            .name(clazz.getPackage().getName() + ".NoOp" + clazz.getSimpleName() + "_" + noOpCount.incrementAndGet())
            .implement(NoOpInstance.class, IService.class)
            .defineConstructor(Visibility.PUBLIC).withParameters(NoOpConstructorArg.class)
            .intercept(MethodCall.invoke(constructor).with(params))
            .method(ElementMatchers.any())
            .intercept(MethodDelegation.to(NoOpInterceptor.class))
            .make()
            .load(clazz.getClassLoader())
            .getLoaded();
    }

    @Override
    public String path() {
        return null;
    }
}
