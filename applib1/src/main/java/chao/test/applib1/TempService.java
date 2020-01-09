package chao.test.applib1;

import chao.app.pool.LifecycleService;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.annotation.Service;

/**
 *
 * TempService.java
 */
@Service(scope = IService.Scope.temp)
public class TempService implements LifecycleService {
}
