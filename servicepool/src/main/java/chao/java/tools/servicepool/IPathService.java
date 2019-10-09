package chao.java.tools.servicepool;

/**
 * @author luqin
 * @since 2019-09-26
 */
public interface IPathService {

    void put(String path, Class<? extends IService> service);

    Class<? extends IService> get(String path);

}
