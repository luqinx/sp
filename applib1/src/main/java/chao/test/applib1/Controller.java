package chao.test.applib1;

import chao.test.appservice.IJavaLib;
import chao.java.tools.servicepool.ServicePool;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class Controller {

    public void control() {
        IJavaLib javaLib = ServicePool.getService(IJavaLib.class);
        javaLib.javaPrint();
    }
}
