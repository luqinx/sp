package chao.test.applib1;

import chao.app.pool.IPriorityService;
import chao.java.tools.servicepool.annotation.Service;

/**
 *
 * PriorityService1.java
 */
@Service(priority = 1)
public class PriorityService1 implements IPriorityService {
    @Override
    public int getPriority() {
        return 1;
    }
}
