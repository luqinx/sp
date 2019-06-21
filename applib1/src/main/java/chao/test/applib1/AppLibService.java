package chao.test.applib1;

import android.util.Log;
import chao.test.appservice.IAppLib;
import chao.java.tools.servicepool.DefaultInitService;

/**
 * @author qinchao
 * @since 2019/4/29
 */
public class AppLibService extends DefaultInitService implements IAppLib {

    @Override
    public void appPrint() {
        Log.i("qinchao", "app lib service");

        new Controller().control();
    }
}
