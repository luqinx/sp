package chao.java.tools.servicepool.combine;

import java.lang.reflect.Method;

/**
 * @author luqin
 * @since 2020-07-31
 */
public interface CombineEmptyHandler<T> {
    void onHandleEmpty(T source, Method method, Object[] args);
}
