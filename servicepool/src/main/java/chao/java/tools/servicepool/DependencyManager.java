package chao.java.tools.servicepool;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public interface DependencyManager {
    void addService(IInitService service);

    void servicesInit();
}
