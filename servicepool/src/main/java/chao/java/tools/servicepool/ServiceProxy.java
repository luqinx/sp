package chao.java.tools.servicepool;

import java.lang.ref.WeakReference;

/**
 * @author qinchao
 * @since 2019/5/5
 */
public class ServiceProxy implements IService {

    private Class<? extends IService> serviceClass;

    private WeakReference<? extends IService> weakService;

    ServiceProxy(Class<? extends IService> clazz) {
        serviceClass = clazz;
    }

    IService getService() {
        if (weakService == null || weakService.get() == null) {
            weakService = new WeakReference<>(ReflectUtil.newInstance(serviceClass));
        }
        return weakService.get();
    }

    Class<?> getServiceClass() {
        return serviceClass;
    }

    @Override
    public String getTag() {
        return getService().getTag();
    }
}
