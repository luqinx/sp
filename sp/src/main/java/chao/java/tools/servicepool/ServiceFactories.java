package chao.java.tools.servicepool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luqin
 * @since  2019-07-13
 */
public abstract class ServiceFactories implements IServiceFactories {

    private Map<String, IServiceFactory> factories = new HashMap<>();


    @Override
    public void addFactory(String pkgName, IServiceFactory serviceFactory) {
        factories.put(pkgName, serviceFactory);
    }

    /**
     * service类的包名
     *
     * @param packageName  类的包名
     * @return  IServiceFactory
     */
    @Override
    public IServiceFactory getServiceFactory(String packageName) {
        return factories.get(packageName);
    }
}
