package chao.java.tools.servicepool;

import java.util.List;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public interface DependencyManager extends IService {
//    void addService(IInitService service);
//
//    void servicesInit();

    void tryInitService(IInitService service, List<Class<? extends IInitService>> dependencies, boolean async);
}
