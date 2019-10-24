package chao.android.tools.servicepool.route;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * todo impl
 *
 * @author luqin
 * @since 2019-10-17
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Autowired {
    String value();
}
