package chao.android.tools.servicepool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import chao.java.tools.servicepool.ServicePool;

/**
 *
 * service pool for android
 *
 * @author qinchao
 * @since 2019/6/19
 */
public class Spa extends ServicePool {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    private static final Object initLock = new Object();

    private static volatile boolean sInited = false;


    public static Context getContext() {
        return sContext;
    }

    public static void init(Context context) {
        if (sInited) {
            return;
        }
        synchronized (initLock) {
            if (sInited) {
                return;
            }
            sContext = context.getApplicationContext();
            if (Looper.myLooper() != Looper.getMainLooper()) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadInitService();
                    }
                });
            } else {
                loadInitService();
            }
            checkLoader();
            sInited = true;
        }
    }

}
