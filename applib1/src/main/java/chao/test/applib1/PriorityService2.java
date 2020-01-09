package chao.test.applib1;

import chao.app.pool.IPriorityService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-01-09
 */
@Service(priority = 1)
public class PriorityService2 implements IPriorityService {
    @Override
    public int getPriority() {
        return 2;
    }
}
