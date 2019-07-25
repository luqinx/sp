package chao.java.tools.servicepool;

import chao.app.interceptor.Interceptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qinchao
 * @since 2019/5/1
 */
public class NoOpInstanceFactory {

    private static final NoOpConstructorArg sConstructorArg = new NoOpConstructorArg();

    private static volatile AtomicInteger noOpCount = new AtomicInteger(0);

    private NoOpInstantiator mInstantiator = null;

    public <T> T newInstance(Class<T> clazz) {
        //如果是接口
        if (clazz.isInterface()) {
            return Interceptor.of(null, clazz).interfaces(NoOpInstance.class).newInstance();
        }
        //如果是类
        try {
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length == 0) {
                return null;
            }
            Constructor<?> constructor = constructors[0];
            for (Constructor tempCon : constructors) {
                if ((tempCon.getModifiers() & Modifier.PUBLIC) != 0
                    || (tempCon.getModifiers() & Modifier.PROTECTED) != 0 ) {
                    constructor = tempCon;
                    break;
                }
            }

            if (constructor != null) {
                Class<?>[] paramTypes = constructor.getParameterTypes();
                Object[] params = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    params[i] = ReflectUtil.getDefaultValue(paramTypes[i]);
                }
                constructor.setAccessible(true); //没什么卵用

                if (mInstantiator == null) {
                    mInstantiator = ServicePool.getService(NoOpInstantiator.class, new DefaultNoOpInstantiator());
                }
                Class<?> noOp = mInstantiator.make(clazz, constructor, params,noOpCount);
                return clazz.cast(noOp.getConstructor(NoOpConstructorArg.class).newInstance(sConstructorArg));
            }
        } catch(Throwable e){
            throw new RuntimeException("NoOpInstance创建失败, " + e.getMessage(), e);
        }
        throw new RuntimeException("NoOpInstance创建失败");
    }
}
