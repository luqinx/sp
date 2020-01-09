package chao.test.applib1;

import chao.app.pool.LifecycleService;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.annotation.Service;

/**
 *
 * OnceService.java
 */
@Service(scope = IService.Scope.once)
public class OnceService implements LifecycleService {
}
