package chao.java.tools.servicepool.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import chao.java.tools.servicepool.IInitService;

/**
 * @author luqin
 * @since 2019-08-05
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Init {
    boolean async() default false;

    Class<? extends IInitService>[] dependencies();
}
