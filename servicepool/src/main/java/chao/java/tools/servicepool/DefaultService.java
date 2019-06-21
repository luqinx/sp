package chao.java.tools.servicepool;

/**
 * @author qinchao
 * @since 2019/5/1
 */
public class DefaultService implements IService {
    @Override
    public String getTag() {
        return getClass().getName();
    }
}
