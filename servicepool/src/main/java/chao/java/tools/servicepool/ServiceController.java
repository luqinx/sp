package chao.java.tools.servicepool;

/**
 * @author qinchao
 * @since 2019/4/29
 */
public interface ServiceController {

    void addService(Class<? extends IService> serviceClass);

    <T extends IService> T getServiceByClass(Class<T> t);

    void loadFinished();

    ServiceProxy<? extends IService> getProxy(Class<? extends IService> clazz);

    void recycleService(Class clazz);
}
