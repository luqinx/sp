package chao.java.tools.servicepool;

/**
 * @author luqin
 * @since 2020/9/6
 */
public interface IServiceLoader extends IService {

    Iterable<Class<? extends IService>> getServices();
}
