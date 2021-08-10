package chao.java.tools.servicepool;

/**
 * 实现类由插件AutoService生成:
 *
 * chao/java/tools/servicepool/gen/ServiceFactoriesInstance
 *
 * @author luqin
 * @since  2019-07-13
 */
public interface IServiceFactories extends IService {
    void addFactory(String pkgName, IServiceFactory serviceFactory);

    IServiceFactory getServiceFactory(String packageName);
}
