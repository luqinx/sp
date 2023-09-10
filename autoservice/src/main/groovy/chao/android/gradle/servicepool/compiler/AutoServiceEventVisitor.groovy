package chao.android.gradle.servicepool.compiler


import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

/**
 *
 * @author luqin* @since 2019-08-29
 */
class AutoServiceEventVisitor extends ClassVisitor {


    AutoServiceEventVisitor(ClassVisitor cv) {
        super(Opcodes.ASM7, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        for (String intf: interfaces) {
            if (Constant.EVENT_ASM_NAME == intf) {
                super.visit(version, access, name, signature, superName, interfaces)
                return
            }
        }
        String[] addInterfaces = new String[interfaces.length + 1]
        System.arraycopy(interfaces, 0, addInterfaces, 0, interfaces.length)
        addInterfaces[addInterfaces.length - 1] = Constant.EVENT_ASM_NAME
        super.visit(version, access, name, signature, superName, addInterfaces)
    }
}
