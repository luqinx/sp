package chao.android.tools.service_pools;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author qinchao
 * @since 2019/4/29
 */
@Service(priority = IService.Priority.MAX_PRIORITY, scope = IService.Scope.once)
public class AppService {

    public void print() {
        System.out.println("I'm app service");
    }
}
