package chao.java.tools.servicepool;

import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * chao.java.tools.servicepool.gen.ServiceFactoriesInstance
 * chao.java.tools.servicepool.gen.InitServiceManagerInstance
 * chao.java.tools.servicepool.gen.PathServicesInstance
 * chao.android.tools.servicepool.AndroidNoOpInstantiator
 *
 * @author luqin
 * @since 2020/9/6
 */
public class BuildInServiceLoader implements IServiceLoader {

    private List<Class<? extends IService>> services;

    public BuildInServiceLoader() {
        services = new ArrayList<>();

        try {
            services.add(Class.forName("chao.java.tools.servicepool.gen.PathServicesInstance").asSubclass(IService.class));
            services.add(Class.forName("chao.java.tools.servicepool.gen.ServiceFactoriesInstance").asSubclass(IService.class));
            services.add(Class.forName("chao.java.tools.servicepool.gen.InitServiceManagerInstance").asSubclass(IService.class));
            services.add(Class.forName("chao.android.tools.servicepool.AndroidNoOpInstantiator").asSubclass(IService.class));
        } catch (ClassNotFoundException e) {
            throw new ServicePoolException("build in services not found.", e);
        }
    }

    @Override
    public Iterable<Class<? extends IService>> getServices() {
        return services;
    }
}
