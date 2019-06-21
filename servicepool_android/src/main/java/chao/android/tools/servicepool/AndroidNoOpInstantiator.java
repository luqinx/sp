package chao.android.tools.servicepool;

import chao.java.tools.servicepool.NoOpConstructorArg;
import chao.java.tools.servicepool.NoOpInstance;
import chao.java.tools.servicepool.NoOpInstantiator;
import chao.java.tools.servicepool.NoOpInterceptor;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicInteger;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author qinchao
 * @since 2019/6/19
 */
public class AndroidNoOpInstantiator implements NoOpInstantiator {


    @Override
    public <T> Class<?> make(Class<T> clazz, Constructor<?> constructor, Object[] params, AtomicInteger noOpCount) {
//        AndroidLazyStrategy.INSTANCE.makeDexDir();
        return new ByteBuddy()
            .subclass(clazz, ConstructorStrategy.Default.NO_CONSTRUCTORS)
            .name(clazz.getPackage().getName() + ".NoOp" + clazz.getSimpleName() + "_" + noOpCount.incrementAndGet())
            .implement(NoOpInstance.class)
            .defineConstructor(Visibility.PUBLIC).withParameters(NoOpConstructorArg.class)
            .intercept(MethodCall.invoke(constructor).with(params))
            .method(ElementMatchers.any()).intercept(MethodDelegation.to(NoOpInterceptor.class))
            .make(AndroidLazyStrategy.INSTANCE)
            .load(clazz.getClassLoader())
            .getLoaded();
    }

    @Override
    public String getTag() {
        return null;
    }
}
