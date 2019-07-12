package chao.java.tools.servicepool;


/**
 * @author qinchao
 * @since 2019/5/1
 */
public class DefaultService implements IService {
    @Override
    public String tag() {
        return getClass().getName();
    }

    @Override
    public int priority() {
        return IService.Priority.NORMAL_PRIORITY;
    }

    @Override
    public int scope() {
        return IService.Scope.global;
    }
}
