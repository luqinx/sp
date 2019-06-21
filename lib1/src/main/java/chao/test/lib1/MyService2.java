package chao.test.lib1;

import chao.java.tools.servicepool.IService;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class MyService2 implements IService {
    @Override
    public String getTag() {
        return "MyService2";
    }
}
