package chao.android.gradle.plugin.assemble

import chao.android.gradle.plugin.base.BasePlugin
import chao.android.gradle.plugin.base.PluginException
import chao.android.gradle.plugin.base.Property
import chao.android.gradle.plugin.Constant
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState
/**
 * @author qinchao
 * @since 2018/11/21
 */
class AssemblePlugin extends BasePlugin {

    AssemblePlugin(Project project) {
        super(project)
    }

    @Override
    def pluginProperties(Property property) {
        return super.pluginProperties(property).load(Constant.extension.MAVEN, project)
    }

    @Override
    void applyRoot() {

        if (!getPluginProperty("publishRelease").match("true")) {
            return
        }

        project.gradle.addListener(new TaskExecutionListener() {
            @Override
            void beforeExecute(Task task) {
                if(task.name != "preReleaseBuild") {
                    return
                }
                task.project.configurations.all { configuration ->
                    configuration.dependencies.all { dependency ->
                        if (dependency && dependency.version && dependency.version.endsWith("-SNAPSHOT")) {
//                            throw new PluginException("发布正式版本不允许使用SNAPSHOT版本依赖: ${dependency.group}:${dependency.name}:${dependency.version}")
                        }
                    }
                }
            }

            @Override
            void afterExecute(Task task, TaskState taskState) {

            }
        })
    }

    @Override
    String bindExtensionName() {
        return Constant.extension.ASSEMBLE
    }

    @Override
    boolean enabledAsDefault() {
        return true
    }
}
