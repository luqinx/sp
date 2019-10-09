package chao.java.tools.servicepool;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public interface Constant {

    interface initState {
        int UNINIT = 0;
        int INITING = 1;
        int INITED = 2;
        int FAILED = 3;
    }
}
