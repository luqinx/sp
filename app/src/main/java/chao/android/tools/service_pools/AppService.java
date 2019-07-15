package chao.android.tools.service_pools;

import chao.java.tools.servicepool.annotation.Service;

/**
 * @author qinchao
 * @since 2019/4/29
 */
@Service
public class AppService {

    public void print() {
        System.out.println("I'm app service");
    }
}
