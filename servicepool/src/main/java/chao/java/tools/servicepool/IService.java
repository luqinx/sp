package chao.java.tools.servicepool;

/**
 * @author qinchao
 * @since 2019/5/1
 */
public interface IService {

//    String path();
//
//    int priority();
//
//    int scope();


    /**
     *  优先级范围 0-5
     *  服务优先级,优先级数值越大优先级越高, 优先级最低是0
     */
    @Deprecated
    interface Priority {
        /**
         * 低优先级
         *
         * @deprecated by {@link ServicePool#MIN_PRIORITY}
         */
        @Deprecated
        int MIN_PRIORITY = 0;

        /**
         * 普通优先级, 默认
         *
         * @deprecated by {@link ServicePool#NORMAL_PRIORITY}
         */
        @Deprecated
        int NORMAL_PRIORITY = 3;

        /**
         * 高优先级
         *
         * @deprecated by {@link ServicePool#MAX_PRIORITY}
         */
        @Deprecated
        int MAX_PRIORITY = 5;
    }

    /**
     *  服务缓存周期
     */
    @Deprecated
    interface Scope {
        /**
         *  全局缓存, 创建后不会被回收，直到进程结束
         *
         * @deprecated by {@link ServicePool#SCOPE_GLOBAL}
         */
        @Deprecated
        int global = SP.SCOPE_GLOBAL;

        /**
         *  临时缓存， 只要不被gc回收，服务对象一直存在
         *  如果被gc回收, 则重新创建
         * @deprecated by {@link ServicePool#SCOPE_WEAK}
         */
        @Deprecated
        int temp = SP.SCOPE_WEAK;

        /**
         *  不会缓存， 每次获取都会重新创建
         *
         *  默认Scope
         * @deprecated by {@link ServicePool#SCOPE_ONCE}
         */
        @Deprecated
        int once = SP.SCOPE_ONCE;
    }
}
