package chao.java.tools.servicepool.annotation;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.NoOpInstance;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个类是一个服务类Service
 *
 * 所有的Service会在编译期通过插件添加到 META-INFO/services/chao.java.tools.servicepool.IService 文件中
 * 通过 {@link chao.java.tools.servicepool.ServiceLoader} 加载,
 * 并通过 {@link chao.java.tools.servicepool.ServiceController} 缓存。
 *
 * RetentionPolicy.RUNTIME 说明:
 * 在和AutoService配合使用时，Service使用RetentionPolicy.Class就够了
 * 在不适用AutoService时,需要使用RetentionPolicy.RUNTIME来获取priority, scope, tag等信息
 *
 * @author qinchao
 * @since 2019/6/21
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    /**
     * 服务优先级
     *
     * 当一个服务接口存在多个实现时， 可以通过优先级来指定使用哪个接口
     *
     * @see IService.Priority
     *
     * @return priority
     */
    int priority() default IService.Priority.NORMAL_PRIORITY;

    /**
     *  服务对象的缓存策略
     *
     * @see IService.Scope
     *
     *  @return scope
     */
    int scope() default IService.Scope.global;

    String tag() default "";

    Class<?> value() default Void.class;


}
