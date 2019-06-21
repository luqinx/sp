package chao.android.tools.service_pools;

import android.app.Application;
import chao.android.tools.servicepool.AndroidServicePool;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.ServicePool;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class App extends Application implements IService {

    public App() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidServicePool.init(this);
        ServicePool.loadServices();
    }

    @Override
    public String getTag() {
        return null;
    }
}
