package chao.android.tools.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import chao.java.tools.servicepool.IService;

/**
 *
 *
 * @author luqin
 * @since 2020-07-28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    /** 路由参数 **/
    String path();
    int flags() default 0;
    String action() default "";
    String dataUri() default "";
    String type() default "";
    int requestCode() default -1;

    /** 自定义参数 **/
    int customFlags() default 0; //可以用来做业务层flag定制，比如是否需要登录
}
