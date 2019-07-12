package chao.test.lib1;

import chao.java.tools.servicepool.DefaultService;
import chao.java.tools.servicepool.IService;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class MyService2 extends DefaultService implements IService {
    @Override
    public String tag() {
        return "MyService2";
    }
}
