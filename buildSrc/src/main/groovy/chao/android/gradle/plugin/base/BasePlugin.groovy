package chao.android.gradle.plugin.base

import chao.android.gradle.plugin.base.extension.DefaultExtension
import chao.android.gradle.plugin.Constant
import chao.android.gradle.plugin.util.logger
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

abstract class BasePlugin<T extends DefaultExtension> implements IPlugin {

    private Project mProject

    private Property mProperties

    BasePlugin(Project project) {
        this.mProject = project
    }

    @Override
    void apply(Project project) {

    }

    void applyRoot() {

    }

    void afterAppEvaluated() {

    }

    void afterAppLibraryEvaluated() {

    }

    void afterJavaEvaluated() {

    }

    boolean isEnabled() {
        //gradle配置
        DefaultExtension extension = getExtension()
        if (extension != null) {
            return extension.isEnabled()
        }

        //查找properties
        if (getPluginProperty("${Constant.propertyKey.PLUGIN_ENABLED}.${bindExtensionName()}").match(true)) {
            return true
        }
        //默认
        return enabledAsDefault()
    }

    T getExtension() {
        if (extensions == null) {
            return null
        }
        return extensions.findByName(bindExtensionName())
    }

    NamedDomainObjectContainer<? extends DefaultExtension> getExtensions() {
        return mProject.extensions.findByName(Constant.extension.AC)
    }

    def extension(NamedDomainObjectContainer<? extends DefaultExtension> extensions) {
        this.extensions = extensions
    }

    abstract String bindExtensionName()

    abstract boolean enabledAsDefault()


    Property.PropertyResult getPluginProperty(String key) {
        return mProperties.propertyResult(key)
    }
    
    def pluginProperties(Property property) {
        mProperties = property
        mProperties.load(bindExtensionName(), project)
    }

    Property getPluginProperties() {
        return mProperties
    }

    def getProject() {
        return mProject
    }

    def getRootProject() {
        return mProject.rootProject
    }

    def getTasks() {
        return project.tasks
    }

    def logd(Object log) {
        def debuggable = extension.getProperty(Constant.propertyKey.DEBUGGABLE)
        new Property.PropertyResult(debuggable).match(true) {
            logger.logd(log)
        }
    }

}