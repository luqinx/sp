package chao.java.tools.servicepool;


/**
 * @author qinchao
 * @since 2019/5/1
 */
public class DefaultService implements IService {
    public String path() {
        return getClass().getName();
    }

    public int priority() {
        return IService.Priority.NORMAL_PRIORITY;
    }

    public int scope() {
        return Scope.once;
    }
}
