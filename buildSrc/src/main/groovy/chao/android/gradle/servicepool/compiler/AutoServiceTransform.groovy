package chao.android.gradle.servicepool.compiler

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
/**
 * @author qinchao
 * @since 2019/6/25
 */
class AutoServiceTransform extends Transform {



    @Override
    String getName() {
        return "AutoService"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        transformInvocation.getOutputProvider().deleteAll()
        transformInvocation.inputs.each { input ->
            input.directoryInputs.each { dirInput ->
//                println(" =========> " + dirInput)
//
//                dirInput.getChangedFiles().each {
//                    println(" =========> " + it)
//                }
            }

            input.jarInputs.each { jarInput ->
                if (jarInput.status != Status.NOTCHANGED) {
                    println(" ==========> " + jarInput)
                }
            }
        }

    }
}
