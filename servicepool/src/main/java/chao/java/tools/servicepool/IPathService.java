package chao.java.tools.servicepool;

/**
 * 实现类由插件AutoService生成
 *
 * chao/java/tools/servicepool/gen/PathServicesInstance
 *
 *
 * @author luqin
 * @since 2019-09-26
 */
public interface IPathService extends IService {

    void put(String path, Class<? extends IService> service);

    Class<? extends IService> get(String path);

}
