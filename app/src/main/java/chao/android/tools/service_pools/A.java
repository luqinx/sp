package chao.android.tools.service_pools;

import chao.java.tools.servicepool.IService;

/**
 * @author qinchao
 * @since 2019/6/25
 */
public class A implements IService {

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
