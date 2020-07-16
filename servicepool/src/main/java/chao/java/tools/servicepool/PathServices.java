package chao.java.tools.servicepool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luqin
 * @since 2019-09-18
 */
public abstract class PathServices extends DefaultService implements IPathService {

    private Map<String, Class<? extends IService>> pathServices;

    public PathServices() {
        pathServices = new ConcurrentHashMap<>();
    }

    @Override
    public void put(String path, Class<? extends IService> service) {
        pathServices.put(path, service);
    }

    @Override
    public Class<? extends IService> get(String path) {
        return pathServices.get(path);
    }
}
