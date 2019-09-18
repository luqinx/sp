package chao.java.tools.servicepool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luqin
 * @since 2019-09-18
 */
public abstract class PathServices extends DefaultService {

    private Map<String, Class<? extends IService>> pathServices;

    public PathServices() {
        pathServices = new HashMap<>();
    }

    public void put(String path, Class<? extends IService> service) {
        pathServices.put(path, service);
    }

    public Class<? extends IService> get(String path) {
        return pathServices.get(path);
    }
}
