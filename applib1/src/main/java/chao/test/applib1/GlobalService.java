package chao.test.applib1;

import chao.app.pool.LifecycleService;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.annotation.Service;

/**
 *
 * GlobalService.java
 */
@Service(scope = IService.Scope.global)
public class GlobalService implements LifecycleService {
}
