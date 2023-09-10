package chao.java.tools.servicepool.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import chao.java.tools.servicepool.ServicePool;

/**
 * 标记一个类是一个服务类Service
 *
 * 通过 {@link chao.java.tools.servicepool.IServiceLoader} 加载,
 * 并通过 {@link chao.java.tools.servicepool.ServiceController} 缓存。
 *
 * RetentionPolicy.RUNTIME 说明:
 * 在和AutoService配合使用时，Service使用RetentionPolicy.Class就够了
 * 在不适用AutoService时,需要使用RetentionPolicy.RUNTIME来获取priority, scope, tag等信息
 *
 *
 * @author qinchao
 * @since 2019/6/21
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Services.class)
public @interface Service {

    /**
     * 服务优先级
     *
     * 当一个服务接口存在多个实现时， 可以通过优先级来指定使用哪个接口
     *
     *
     * @return priority
     */
    int priority() default ServicePool.NORMAL_PRIORITY;

    /**
     *  服务对象的缓存策略
     *
     *
     *  @return scope
     */
    int scope() default ServicePool.SCOPE_ONCE;

    String path() default "";

    Class<?> value() default Void.class;

    /**
     *  @return 当inherited为true时, 被Service标记的类的子类也会被当做一个Service
     */
    boolean inherited() default false;

    boolean disableIntercept() default false;
}
