package chao.java.tools.servicepool;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import chao.android.tools.interceptor.Interceptor;

/**
 * @author qinchao
 * @since 2019/5/1
 */
public class NoOpInstanceFactory {

    private static final NoOpConstructorArg sConstructorArg = new NoOpConstructorArg();

    private static volatile AtomicInteger noOpCount = new AtomicInteger(0);

    private NoOpInstantiator mInstantiator = null;

    private Map<Class, NoOpInstance> noOpCache = new ConcurrentHashMap<>();

    public <T> T newInstance(Class<T> clazz) {
        T t = clazz.cast(noOpCache.get(clazz));
        if (t != null) {
            return t;
        }
        //如果是接口
        if (clazz.isInterface()) {
            t = Interceptor.of(null, clazz).interfaces(NoOpInstance.class).newInstance();
            noOpCache.put(clazz, (NoOpInstance) t);
            return t;
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
                Class<?> noOp = mInstantiator.make(clazz, constructor, params, noOpCount);
                t = clazz.cast(noOp.getConstructor(NoOpConstructorArg.class).newInstance(sConstructorArg));
                noOpCache.put(clazz, (NoOpInstance) t);
                return t;
            }
        } catch(Throwable e){
            System.out.println("no op failed : " + clazz.getName());
            throw new RuntimeException("NoOpInstance创建失败, " + e.getMessage(), e);
        }
        throw new RuntimeException("NoOpInstance创建失败");
    }
}
