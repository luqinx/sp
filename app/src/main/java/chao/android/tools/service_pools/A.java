package chao.android.tools.service_pools;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author qinchao
 * @since 2019/6/25
 */
@Service
public class A implements IA, IService {

    private int a;

    {
        a = 10;
    }

    public A() {
        a = 100;
    }

    public int getInt() {
        return 10086;
    }
}
