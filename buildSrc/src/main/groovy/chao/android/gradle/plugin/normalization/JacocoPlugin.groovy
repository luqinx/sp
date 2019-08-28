package chao.android.gradle.plugin.normalization

import chao.android.gradle.plugin.base.BasePlugin
import chao.android.gradle.plugin.Constant
import org.gradle.api.Project
import org.gradle.testing.jacoco.tasks.JacocoReport
/**
 *
 * execute command for android:
 *   ./gradlew :<android project name>:createDebugAndroidTestCoverageReport
 *
 * execute command for java:
 *   ./gradlew :<java project name>:build
 *
 * @author qinchao
 * @since 2019/4/16
 */
class JacocoPlugin extends BasePlugin {

    JacocoPlugin(Project project) {
        super(project)
    }

    @Override
    void afterAppLibraryEvaluated() {
        super.afterAppLibraryEvaluated()
        androidJacoco()
    }

    @Override
    void afterAppEvaluated() {
        super.afterAppEvaluated()
        androidJacoco()
    }

    @Override
    void afterJavaEvaluated() {
        super.afterJavaEvaluated()

        project.apply plugin: 'jacoco'

        JacocoReport jacocoReport = project.tasks.findByName("jacocoTestReport")
        jacocoReport.reports {
            html.enabled true
            xml.enabled false
        }
        project.tasks.findByName("check").dependsOn(jacocoReport)
    }

    @Override
    String bindExtensionName() {
        return Constant.extension.JACOCO
    }

    @Override
    boolean enabledAsDefault() {
        return false
    }

    private void androidJacoco() {

        project.android.buildTypes {
            debug.testCoverageEnabled true
            release.testCoverageEnabled true
        }
    }

}
