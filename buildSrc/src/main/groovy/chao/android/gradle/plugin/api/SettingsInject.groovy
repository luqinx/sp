package chao.android.gradle.plugin.api

import chao.android.gradle.plugin.dependencies.ModuleHandler
import org.gradle.api.Action
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.api.internal.SettingsInternal
import org.gradle.api.internal.initialization.ClassLoaderScope
import org.gradle.api.internal.initialization.ScriptHandlerFactory
import org.gradle.api.internal.plugins.DefaultObjectConfigurationAction
import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.configuration.ScriptPlugin
import org.gradle.configuration.ScriptPluginFactory
import org.gradle.groovy.scripts.ScriptSource
import org.gradle.groovy.scripts.TextResourceScriptSource
import org.gradle.internal.resource.BasicTextResourceLoader
import org.gradle.internal.resource.TextResource
import org.gradle.invocation.DefaultGradle

import java.lang.reflect.Field

/**
 * @author qinchao
 * @since 2019/5/29
 */
class SettingsInject {

    private static final String modulesSettingsFile = "modules.gradle"

    private static File modulesFile

    static void inject(Settings settings, DefaultGradle gradle) {
//        Map<String, Object> map = new HashMap<>(1)
//        map.put("from", new File(settings.getRootDir(), modulesSettingsFile))
//        gradle.apply(map)

        modulesFile = new File(settings.getRootDir(), modulesSettingsFile)


        gradle.apply(new Action<ObjectConfigurationAction>() {
            @Override
            void execute(ObjectConfigurationAction objectConfigurationAction) {

                try {
                    Field scriptHandlerFactoryField = DefaultObjectConfigurationAction.class.getDeclaredField("scriptHandlerFactory")
                    Field configureFactoryField = DefaultObjectConfigurationAction.class.getDeclaredField("configurerFactory")

                    scriptHandlerFactoryField.setAccessible(true)
                    configureFactoryField.setAccessible(true)
                    ScriptHandlerFactory handlerFactory = (ScriptHandlerFactory) scriptHandlerFactoryField.get(objectConfigurationAction)
                    ScriptPluginFactory configurerFactory = configureFactoryField.get(objectConfigurationAction)

                    applySettingsScript(handlerFactory, configurerFactory, settings)
                } catch (NoSuchFieldException e) {
                    e.printStackTrace()
                } catch (IllegalAccessException e) {
                    e.printStackTrace()
                }

            }
        })


    }

    private static void applySettingsScript(ScriptHandlerFactory scriptHandlerFactory, ScriptPluginFactory configurerFactory, final SettingsInternal settings) {
        TextResource settingsResource = (new BasicTextResourceLoader()).loadFile("settings file", modulesFile)
        ScriptSource settingsScriptSource = new TextResourceScriptSource(settingsResource)
        ClassLoaderScope settingsClassLoaderScope = settings.getClassLoaderScope()
        ScriptHandler scriptHandler = scriptHandlerFactory.create(settingsScriptSource, settingsClassLoaderScope)
        ScriptPlugin configurer = configurerFactory.create(settingsScriptSource, scriptHandler, settingsClassLoaderScope, settings.getRootClassLoaderScope(), true)
        ModuleHandler handler = ModuleHandler.instance()
        handler.setSettings(settings)
        configurer.apply(handler)
    }

}
