package chao.android.tools.rpc;

/**
 * @author luqin
 * @since 2020-08-03
 */
public interface RemoteCallbackHandler<T> {
    void resolve(T result);
}
