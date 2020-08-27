package chao.test.applib1;

import chao.app.pool.IPathInstanceService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * 组件A中
 *
 * PathService
 */
@Service(path = "pathService")
public class PathService implements IPathInstanceService {
    @Override
    public String pathServiceName() {
        return "Path Service";
    }
}
