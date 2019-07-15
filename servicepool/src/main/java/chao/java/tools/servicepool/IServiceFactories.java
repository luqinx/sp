package chao.java.tools.servicepool;

/**
 * @author luqin  qinchao@mochongsoft.com
 * @project: zmjx-sp
 * @description:
 * @date 2019-07-13
 */
public interface IServiceFactories extends IService {
    void addFactory(String pkgName, IServiceFactory serviceFactory);

    IServiceFactory getServiceFactory(String packageName);
}
