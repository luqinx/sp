package chao.java.tools.servicepool;

/**
 * @author luqin
 * @since  2019-07-13
 */
public interface IServiceFactories extends IService {
    void addFactory(String pkgName, IServiceFactory serviceFactory);

    IServiceFactory getServiceFactory(String packageName);
}
