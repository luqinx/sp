package chao.java.tools.servicepool.combine;

import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2019-09-30
 */
public interface CombineCallback<T> {
    void onContinue(T t);
    void onUserInterrupt(T t, IService service);
    void onInterrupt(T t, Throwable e);
}
