package chao.android.gradle.plugin.base

import chao.android.gradle.plugin.api.UtilExtension
import chao.android.gradle.plugin.base.extension.DefaultExtension
import chao.android.gradle.plugin.base.extension.ExtensionFactory
import chao.android.gradle.plugin.Constant
import chao.android.gradle.plugin.util.logger
import org.gradle.api.Project

class PluginManager {

    Project rootProject

    List<Class<? extends BasePlugin>> plugins

    PluginManager(Project project) {
        this.rootProject = project
        plugins = new ArrayList<>()
    }

    void apply() {
        
        rootProject.allprojects { project ->
            def extensionContainer = project.container(DefaultExtension, new ExtensionFactory())
            project.extensions.add(Constant.extension.AC, extensionContainer)
        }

        rootProject.extensions.create("acUtil", UtilExtension, )

        plugins.each { pluginClass ->

            //root project
            def rootPlugin = pluginClass.newInstance(rootProject)
            rootPlugin.pluginProperties Env.properties
            rootPlugin.applyRoot()


            rootProject.subprojects { subproject ->

                BasePlugin plugin = pluginClass.newInstance(subproject)
                Property property = new Property()
                plugin.pluginProperties property

                subproject.afterEvaluate {

                    logger.logd("${subproject.name}.${plugin.bindExtensionName()} is enabled? ${plugin.isEnabled()}")
                    if (!plugin.isEnabled()) {
                        return
                    }

                    //all projects
                    plugin.apply(subproject)

                    //android app projects
                    if (subproject.plugins.hasPlugin("com.android.application")) {
                        plugin.afterAppEvaluated()
                    } else if (subproject.plugins.hasPlugin("com.android.library")) {
                        plugin.afterAppLibraryEvaluated()
                    } else if (subproject.plugins.hasPlugin("java")
                        || subproject.plugins.hasPlugin("java-library")) {
                        plugin.afterJavaEvaluated()
                    }
                }
            }
        }

    }

    void registerPlugin(Class<? extends BasePlugin> clazz) {
        if (!plugins.contains(clazz)) {
            plugins.add(clazz)
        }
    }

    void unregisterPlugin(Class<? extends BasePlugin> clazz) {
        plugins.remove(clazz)
    }
}