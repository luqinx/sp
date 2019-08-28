package chao.android.gradle.plugin

import chao.android.gradle.plugin.assemble.AssemblePlugin
import chao.android.gradle.plugin.base.Env
import chao.android.gradle.plugin.base.PluginManager
import chao.android.gradle.plugin.base.Property
import chao.android.gradle.plugin.dependencies.DependencyPlugin
import chao.android.gradle.plugin.maven.MavenPlugin
import chao.android.gradle.plugin.normalization.CheckStylePlugin
import chao.android.gradle.plugin.normalization.JacocoPlugin
import chao.android.gradle.plugin.normalization.LintPlugin
import chao.android.gradle.plugin.version.ACVersionPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class EntryPoint implements Plugin<Project> {

    private PluginManager pluginManager

    @Override
    void apply(Project project) {

        if (project != project.rootProject) {
            return
        }

        Env.rootProject project

        Property prop = new Property()
        prop.initStaticProperties()
        Env.properties(prop)
        Env.debug(Env.properties.propertyResult(Constant.propertyKey.DEBUGGABLE).booleanValue(false))

        pluginManager = new PluginManager(project)

        pluginManager.registerPlugin(DependencyPlugin)
        pluginManager.registerPlugin(ACVersionPlugin)
        pluginManager.registerPlugin(MavenPlugin)
        pluginManager.registerPlugin(AssemblePlugin)
        pluginManager.registerPlugin(CheckStylePlugin)
        pluginManager.registerPlugin(LintPlugin)
        pluginManager.registerPlugin(JacocoPlugin)

        pluginManager.apply()
    }
}