package chao.android.tools.service_pools.path;

import android.util.Log;

import chao.android.tools.service_pools.Printer;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-06-16
 */
@Service(path = "/app/repeat")
@Service(path = "/app/repeatable")
public class RepeatePathService implements Printer,IRepeat {
    @Override
    public void print() {
        System.out.println("this is a repeat path service.");
    }
}
