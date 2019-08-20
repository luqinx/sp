package chao.android.gradle.plugin.base

/**
 * @author qinchao
 * @since 2018/11/13
 */
class PluginException extends RuntimeException {

    PluginException(String message) {
        super("abkit plugin: " + message)
    }

    PluginException(String message, Throwable e) {
        super(message, e)
    }
}
