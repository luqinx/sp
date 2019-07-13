package chao.android.gradle.servicepool.compiler

import chao.android.gradle.servicepool.hunter.HunterTransform
import org.gradle.api.Project

/**
 * @author qinchao
 * @since 2019/6/25
 */
class AutoServiceTransform extends HunterTransform {

    private AutoServiceWeaver autoServiceWeaver


    AutoServiceTransform(Project project) {
        super(project)
        autoServiceWeaver = new AutoServiceWeaver()
        this.bytecodeWeaver = autoServiceWeaver
    }

    @Override
    protected void transformFinished(File destJar) {
//        autoServiceWeaver.transformFinished(destJar)
    }
}
