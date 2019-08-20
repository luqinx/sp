package chao.android.gradle.plugin.normalization

import chao.android.gradle.plugin.base.BasePlugin
import chao.android.gradle.plugin.Constant
import org.gradle.api.Project
/**
 * @author qinchao
 * @since 2019/2/28
 */
class LintPlugin extends BasePlugin {

    LintPlugin(Project project) {
        super(project)
    }

    @Override
    void afterAppEvaluated() {
        super.afterAppEvaluated()

        project.android.lintOptions {
            abortOnError false
            xmlReport false
            checkDependencies true
        }
    }

    @Override
    String bindExtensionName() {
        return Constant.extension.LINT
    }

    @Override
    boolean enabledAsDefault() {
        return false
    }
}
