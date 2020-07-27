package chao.android.tools.servicepool.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luqin
 * @since 2020-07-27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteServiceConfig {
    boolean forceMainThread() default false;

    /**
     * 远程Service组件packageName
     */
    String remotePackageName();

    /**
     *  远程Service组件componentName
     */
    String remoteComponentName();

    /**
     * 数据传输超时
     */
    int timeout() default 3000;
}
