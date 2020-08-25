package chao.java.tools.servicepool.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import chao.java.tools.servicepool.IInitService;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.Sp;

/**
 * @author luqin
 * @since 2019-08-05
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Init {
    /**
     * 是否异步初始化
     */
    boolean async() default false;

    /**
     * 是否懒加载模式， 默认懒加载
     *
     * 懒加载模式会在组件第一次被调用的时候初始化，否则
     * 在ServicePool.init()函数中执行初始化。
     *
     * {@link ServicePool#init()}
     *
     */
    boolean lazy() default true;


    int priority() default Sp.NORMAL_PRIORITY;

    /**
     * 依赖组件
     *
     * 如果有依赖组件,依赖组件先初始化后,本组件才开始初始化
     */
    Class<? extends IInitService>[] dependencies() default {};
}
