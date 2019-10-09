package chao.android.tools.servicepool;

import java.util.List;

import chao.java.tools.servicepool.DependencyManager;
import chao.java.tools.servicepool.IInitService;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class AndroidDependencyManager implements DependencyManager {

    @Override
    public void tryInitService(IInitService service, List<Class<? extends IInitService>> dependencies, boolean async) {

    }
}
