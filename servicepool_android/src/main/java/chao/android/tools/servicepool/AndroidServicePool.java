package chao.android.tools.servicepool;

import android.annotation.SuppressLint;
import android.content.Context;
import chao.java.tools.servicepool.ServicePool;

/**
 * @author qinchao
 * @since 2019/6/19
 */
public class AndroidServicePool extends ServicePool {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    public static void init(Context context) {
        sContext = context.getApplicationContext();
        loadServices();
    }
}
