package chao.java.tools.servicepool;

import java.lang.reflect.Constructor;

/**
 * @author qinchao
 * @since 2019/5/5
 */
public class ReflectUtil {

    public static <T> T newInstance(Class<T> serviceClass) {
        try {
            Constructor<?> constructor = serviceClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return serviceClass.cast(constructor.newInstance());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getDefaultValue(Class<?> type) {
        if (Number.class.isAssignableFrom(type)) {
            if (Float.class.isAssignableFrom(type)) {
                return 0.0f;
            } else if (Long.class.isAssignableFrom(type)) {
                return 0L;
            } else if (Short.class.isAssignableFrom(type)) {
                return (short) 0;
            } else if (Double.class.isAssignableFrom(type)) {
                return 0.0;
            }
            return 0;
        } else if (boolean.class.isAssignableFrom(type)) {
            return false;
        } else if (Object.class.isAssignableFrom(type)) {
            return null;
        }
        return 0;
    }
}
