package chao.android.gradle.servicepool.compiler;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;

import chao.java.tools.servicepool.IService;

/**
 *
 * @author luqin  qinchao@mochongsoft.com
 * @since 2019-07-09
 */
class AutoServiceVisitor extends ClassVisitor implements Constant{


    private Map<String, Object> values;

    AutoServiceVisitor(ClassVisitor cv, Map<String, Object> values) {
        super(Opcodes.ASM6, cv);
        this.values = values;
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
        addInterfaces[addInterfaces.length - 1] = IService.class.getName().replaceAll("\\.", "/");
//        Logger.log("visit", name, Arrays.toString(interfaces), Arrays.toString(addInterfaces));
        super.visit(version, access, name, signature, superName, addInterfaces);
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        return super.visitModule(name, access, version);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new MethodWrapper(mv);
    }


    @Override
    public void visitEnd() {
//        增加scope, priority, tag三个方法
//        MethodVisitor method = cv.visitMethod(Opcodes.ACC_PUBLIC, "scope", "", null, null);
        super.visitEnd();
    }

    @Override
    public void visitSource(String source, String debug) {
        super.visitSource(source, debug);
    }

    private class MethodWrapper extends MethodVisitor {

        public MethodWrapper(MethodVisitor mv) {
            super(Opcodes.ASM6, mv);
        }

        @Override
        public void visitCode() {
            super.visitCode();
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
//            Logger.log("visitMethodInsn", name, owner, desc);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            super.visitIntInsn(opcode, operand);
        }
    }
}
