package chao.android.tools.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luqin
 * @since 2020-07-28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    String path();
    int flags() default 0;
    String action() default "";
    String dataUri() default "";
    String type() default "";
    int requestCode() default -1;
}
