package chao.android.tools.service_pools.test;

import chao.android.tools.service_pools.CommonPrinter;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.Sp;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-07-22
 */
@Service(priority = Sp.MAX_PRIORITY)
public class Haha extends CommonPrinter {

    public Haha() {

    }

    @Override
    public void print() {
        System.out.println("I'm Haha");
    }
}
