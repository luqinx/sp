package chao.java.tools.servicepool;

/**
 * @author qinchao
 * @since 2019/4/29
 */
public interface ServiceController {

    void addService(Class<? extends IService> serviceClass);

    <T> T getServiceByClass(Class clazz, Class<T> t);

    void loadFinished();

}
