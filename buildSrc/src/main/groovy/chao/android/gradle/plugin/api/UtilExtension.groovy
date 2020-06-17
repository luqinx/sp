package chao.android.gradle.plugin.api

import chao.android.gradle.plugin.base.Env

/**
 * @author qinchao
 * @since 2019/4/24
 */
class UtilExtension {

    static String property(String key) {
        return Env.properties.propertyResult(key).value
    }
}
