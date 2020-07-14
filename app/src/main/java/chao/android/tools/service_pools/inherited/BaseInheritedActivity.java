package chao.android.tools.service_pools.inherited;

import chao.app.ami.base.AMIActivity;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-07-13
 */
@Service(inherited = true, priority = IService.Priority.MIN_PRIORITY)
public abstract class BaseInheritedActivity extends AMIActivity implements IService {
}
