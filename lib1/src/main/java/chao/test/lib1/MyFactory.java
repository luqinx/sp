package chao.test.lib1;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class MyFactory implements IServiceFactory {

    private List<IService> list = new ArrayList<>();

    public MyFactory() {
        list.add(new MyService1());
        list.add(new MyService2());
    }

    @Override
    public Iterable<Class<? extends IService>> createServices() {
        return Arrays.asList(MyService1.class, MyService2.class);
    }
}
