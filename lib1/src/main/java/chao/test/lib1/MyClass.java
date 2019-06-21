package chao.test.lib1;

import chao.test.appservice.IJavaLib;
import chao.java.tools.servicepool.DefaultInitService;

public class MyClass extends DefaultInitService implements IJavaLib {
    @Override
    public void javaPrint() {
        System.out.println("java lib service ");
    }
}
