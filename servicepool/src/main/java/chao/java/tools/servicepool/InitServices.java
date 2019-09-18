package chao.java.tools.servicepool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luqin
 * @since 2019-09-17
 */
public abstract class InitServices extends DefaultService {

    private List<Class<? extends IInitService>> initServices;

    public InitServices() {
        initServices = new ArrayList<>();
    }

    public void addInitService(Class<? extends IInitService> initService) {
        initServices.add(initService);
    }

    void initService() {
        for (Class<? extends IInitService> clazz: initServices) {
            //getService会唤起Service的init
            ServicePool.getService(clazz);
        }
    }

    @Override
    public int scope() {
        return Scope.once;
    }
}
