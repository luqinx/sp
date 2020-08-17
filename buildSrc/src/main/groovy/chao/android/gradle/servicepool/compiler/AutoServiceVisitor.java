package chao.android.gradle.servicepool.compiler;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 *
 *
 * @author luqin
 * @since 2019-07-09
 */
class AutoServiceVisitor extends ClassVisitor implements Constant{


    AutoServiceVisitor(ClassVisitor cv) {
        super(Opcodes.ASM6, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        for (String intf: interfaces) {
            if (SERVICE_ASM_NAME.equals(intf)) {
                super.visit(version, access, name, signature, superName, interfaces);
                return;
            }
        }
        String[] addInterfaces = new String[interfaces.length + 1];
        System.arraycopy(interfaces, 0, addInterfaces, 0, interfaces.length);
        addInterfaces[addInterfaces.length - 1] = SERVICE_FULL_NAME.replaceAll("\\.", "/");
        super.visit(version, access, name, signature, superName, addInterfaces);
    }
}
