package chao.java.tools.servicepool;

/**
 * @author qinchao
 * @since 2019/5/1
 */
public interface IService {

    String tag();

    int priority();

    int scope();


    /**
     *  优先级范围 0-5
     *  服务优先级,优先级数值越大优先级越高, 优先级最低是0
     */
    interface Priority {
        /**
         * 低优先级
         */
        int MIN_PRIORITY = 0;

        /**
         * 普通优先级, 默认
         */
        int NORMAL_PRIORITY = 3;

        /**
         * 高优先级
         */
        int MAX_PRIORITY = 5;
    }

    /**
     *  服务缓存周期
     */
    interface Scope {
        /**
         *  全局缓存, 创建后不会被回收，直到进程结束
         *
         *  默认Scope
         */
        int global = 0;

        /**
         *  临时缓存， 只要不被gc回收，服务对象一直存在
         *  如果被gc回收, 则重新创建
         */
        int temp = 1;

        /**
         *  不会缓存， 每次获取都会重新创建
         */
        int once = 2;
    }
}
