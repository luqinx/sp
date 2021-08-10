package chao.java.tools.servicepool;

/**
 * @author qinchao
 * @since 2019/4/29
 */
public interface ServiceController {


    <T extends IService> T getFixedServiceByClass(Class<T> t);

    <T extends IService> T getServiceByClass(Class<T> t);

    void loadFinished();

    ServiceProxy<? extends IService> getProxy(Class<? extends IService> clazz);

    void recycleService(Class<?> clazz);
}
