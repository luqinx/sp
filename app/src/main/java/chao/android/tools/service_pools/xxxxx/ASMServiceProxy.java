package chao.android.tools.service_pools.xxxxx;

import java.util.ArrayList;

import chao.android.tools.service_pools.test.InitService1;
import chao.android.tools.service_pools.test.InitService2;
import chao.android.tools.service_pools.test.InitService3;
import chao.java.tools.servicepool.IInitService;
import chao.java.tools.servicepool.ServiceProxy;

/**
 * @author luqin
 * @since 2019-08-28
 */
public class ASMServiceProxy {



    private void newServiceProxy() {
        ArrayList<Class<? extends IInitService>> services = new ArrayList<>();
        services.add(InitService1.class);
        services.add(InitService2.class);

        ServiceProxy proxy = new ServiceProxy(InitService3.class, null, 100, 200, "hah", true, services);

        ASMStaticClass  clzz = new ASMStaticClass();
    }
}
