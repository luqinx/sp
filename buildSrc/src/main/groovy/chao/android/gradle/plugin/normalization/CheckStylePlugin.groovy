package chao.android.gradle.plugin.normalization

import chao.android.gradle.plugin.base.BasePlugin
import chao.android.gradle.plugin.Constant
import chao.android.gradle.plugin.util.Util
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.tasks.TaskState
/**
 *
 *  execute command for checkstyle
 *    ./gradlew check
 *
 * @author qinchao
 * @since 2019/2/27
 */
class CheckStylePlugin extends BasePlugin {

    CheckStylePlugin(Project project) {
        super(project)
    }

    @Override
    void afterAppEvaluated() {
        super.afterAppEvaluated()

        project.apply 'plugin': 'checkstyle'

        File checkConfig = new File(Util.getCheckStyleDir(project), "checkstyle.xml")

        CheckstyleExtension extension = (CheckstyleExtension) project.extensions.findByName("checkstyle")
        extension.toolVersion '8.18'
        extension.ignoreFailures true
        extension.showViolations false
        extension.configFile = checkConfig


        project.gradle.addListener(new TaskExecutionListener() {
            @Override
            void beforeExecute(Task task) {
                if (task.name != 'checkstyle' && task.name != 'check') {
                    return
                }

                if (!checkConfig.exists()) {
                    Util.copyResource("checkstyle/ac-check.xml", checkConfig)
                }

//                project.apply 'plugin': 'checkstyle'


            }

            @Override
            void afterExecute(Task task, TaskState taskState) {

            }
        })


        Checkstyle checkTask = project.tasks.create("checkstyle", Checkstyle, new Action<Checkstyle>() {

            @Override
            void execute(Checkstyle checkstyle) {

                rootProject.subprojects {
                    String srcDir = "../" + it.name + "/src"
                    checkstyle.source(srcDir)
                }

                checkstyle.include('**/*.java')
                checkstyle.exclude('**/gen/**')
                checkstyle.exclude('**/test/**')
                checkstyle.exclude("**/androidTest/**")
                checkstyle.setClasspath(project.files())

//                println(checkstyle.getSource().files)
            }
        })

        project.tasks.findByName("check").dependsOn(checkTask)

    }

    @Override
    String bindExtensionName() {
        return Constant.extension.CHECKSTYLE
    }

    @Override
    boolean enabledAsDefault() {
        return false
    }
}
