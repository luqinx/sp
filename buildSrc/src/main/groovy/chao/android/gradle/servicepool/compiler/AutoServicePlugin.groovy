package chao.android.gradle.servicepool.compiler

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author qinchao
 * @since 2019/6/25
 */
class AutoServicePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        AppExtension android = project.extensions.getByName("android")
        android.registerTransform(new AutoServiceTransform(project))
    }
}
