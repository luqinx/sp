package chao.java.tools.servicepool;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public interface Constant {

    interface initState {
        int UNINIT = 0;
        int TRYING = 1;
        int INITING = 2;
        int INITED = 3;
        int FAILED = 4;
    }
}
