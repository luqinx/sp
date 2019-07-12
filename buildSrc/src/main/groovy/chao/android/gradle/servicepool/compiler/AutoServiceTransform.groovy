package chao.android.gradle.servicepool.compiler

import chao.android.gradle.servicepool.hunter.HunterTransform
import org.gradle.api.Project

/**
 * @author qinchao
 * @since 2019/6/25
 */
class AutoServiceTransform extends HunterTransform {


    AutoServiceTransform(Project project) {
        super(project)
        this.bytecodeWeaver = new AutoServiceWeaver()
    }


}
