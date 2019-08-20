package chao.android.gradle.plugin.version

import chao.android.gradle.plugin.base.BasePlugin
import chao.android.gradle.plugin.Constant
import chao.android.gradle.plugin.util.Util
import org.gradle.api.Project

class ACVersionPlugin extends BasePlugin<ACVersionExtension> {

    ACVersionPlugin(Project project) {
        super(project)
    }

    void applyRoot() {
        rootProject.allprojects { subProject ->

            subProject.afterEvaluate {
                if (!Util.isAndroid(subProject)) {
                    return
                }
                if (extension == null) {
                    return
                }
                if (!subProject.android.compileSdkVersion || extension.force) {
                    subProject.android.compileSdkVersion extension.compileSdkVersion
                }
                if (!subProject.android.buildToolsVersion|| extension.force) {
                    subProject.android.buildToolsVersion = extension.buildToolsVersion
                }
                if (!subProject.android.defaultConfig.minSdkVersion || extension.force) {
                    subProject.android.defaultConfig.minSdkVersion extension.minSdkVersion
                }
                if (!subProject.android.defaultConfig.targetSdkVersion || extension.force) {
                    subProject.android.defaultConfig.targetSdkVersion extension.targetSdkVersion
                }
            }
        }
    }


    @Override
    String bindExtensionName() {
        return Constant.extension.AC_VERSION
    }

    @Override
    boolean enabledAsDefault() {
        return true
    }
}