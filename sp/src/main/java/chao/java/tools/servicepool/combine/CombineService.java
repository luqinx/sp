package chao.java.tools.servicepool.combine;


/**
 * @author luqin
 * @since 2019-09-30
 */
public interface CombineService<T> extends Iterable<T> {
    int size();

    T get(int index);

    T setEmptyHandler(CombineEmptyHandler<T> handler);
}
