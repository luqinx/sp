package chao.android.tools.service_pools;

import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @date 2019-07-17
 */
@Service
public class CommonPrinter implements Printer {

    @Override
    public void print() {
        System.out.println("I'm a common Printer.");
    }
}
