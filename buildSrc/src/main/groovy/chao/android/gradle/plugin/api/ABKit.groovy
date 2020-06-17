package chao.android.gradle.plugin.api

import chao.android.gradle.plugin.base.Env
import org.gradle.api.initialization.Settings
import org.gradle.invocation.DefaultGradle

class ABKit {

    static String property(String key) {
        return Env.properties.propertyResult(key).value
    }

    static void inject(Settings settings, DefaultGradle gradle) {
        SettingsInject.inject(settings, gradle)
    }
}
