package chao.android.gradle.servicepool.compiler

import chao.android.gradle.servicepool.hunter.HunterTransform
import com.android.build.api.transform.Context
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import org.apache.commons.io.IOUtils
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
        AutoServiceExtension extension = project.extensions.create(Constant.EXTENSION_NAME, AutoServiceExtension)
        autoServiceWeaver.setExtension(extension)
    }


    @Override
    protected void transformFinished(File destJar) {
        System.out.println("add autoservice generate jar: " + destJar.path)
        autoServiceWeaver.transformFinished(destJar)
        System.out.println("transformFinished ")

//        IOUtils.copy(new FileInputStream(destJar), new )
    }

    @Override
    boolean isIncremental() {
        return false
    }
}
