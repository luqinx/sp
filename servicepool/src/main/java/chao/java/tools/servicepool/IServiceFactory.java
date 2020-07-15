package chao.java.tools.servicepool;

import java.util.HashSet;

/**
 * @author qinchao
 * @since 2019/5/4
 *
 * @in
 */
public interface IServiceFactory {

    HashSet<ServiceProxy> createServiceProxies(Class<?> clazz);

    ServiceProxy<? extends IService> createServiceProxy(Class<?> clazz);

    IService createInstance(Class<?> clazz);
}
