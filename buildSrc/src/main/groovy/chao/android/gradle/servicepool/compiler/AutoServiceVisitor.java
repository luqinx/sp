package chao.android.gradle.servicepool.compiler;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.Map;

import chao.android.gradle.servicepool.Logger;
import chao.java.tools.servicepool.IService;

import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.IRETURN;

/**
 *
 * @author luqin  qinchao@mochongsoft.com
 * @since 2019-07-09
 */
class AutoServiceVisitor extends ClassVisitor {

    private static final String METHOD_SCOPE = "scope";

    private static final String METHOD_PRIORITY = "priority";

    private static final String METHOD_TAG = "tag";

    private static final String METHOD_VALUE = "value";

    private Map<String, Object> values;

    AutoServiceVisitor(ClassVisitor cv, Map<String, Object> values) {
        super(Opcodes.ASM6, cv);
        this.values = values;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        String[] addInterfaces = new String[interfaces.length + 1];
        System.arraycopy(interfaces, 0, addInterfaces, 0, interfaces.length);
        addInterfaces[addInterfaces.length - 1] = IService.class.getName().replaceAll("\\.", "/");
//        Logger.log("visit", name, Arrays.toString(interfaces), Arrays.toString(addInterfaces));

        MethodVisitor methodValue = visitMethod(Opcodes.ACC_PUBLIC, "sp$$" + METHOD_VALUE, "()Ljava/lang/String;", null, null);
        if (methodValue != null) {
            methodValue.visitCode();
            Object value = values.get(METHOD_VALUE);
            if (value == null) {
                value = "";
            }
            methodValue.visitLdcInsn(value);
            methodValue.visitInsn(ARETURN);
            methodValue.visitEnd();
        }

        MethodVisitor methodScope = visitMethod(Opcodes.ACC_PUBLIC, METHOD_SCOPE, "()I", null, null);
        if (methodScope != null) {
            methodScope.visitCode();
            Object scope = values.get(METHOD_SCOPE);
            if (scope == null) {
                scope = IService.Scope.global;
            }
            methodScope.visitInsn((int) scope + 3);
            methodScope.visitInsn(IRETURN);
            methodScope.visitEnd();
        }
//
        MethodVisitor methodPriority = visitMethod(Opcodes.ACC_PUBLIC, METHOD_PRIORITY, "()I", null, null);
        if (methodPriority != null) {
            methodPriority.visitCode();
            //ICONST_0...5
            Object priority = values.get(METHOD_PRIORITY);
            if (priority == null) {
                priority = IService.Priority.NORMAL_PRIORITY;
            }
            if ((int) priority > 5) {
                throw new IllegalArgumentException("max priority value is 5.");
            }
            methodPriority.visitInsn((int)priority + 3);
            methodPriority.visitInsn(IRETURN);
            methodPriority.visitEnd();
        }

        MethodVisitor methodTag = visitMethod(Opcodes.ACC_PUBLIC, METHOD_TAG, "()Ljava/lang/String;", null, null);
        if (methodTag != null) {
            methodTag.visitCode();
            Object tag = values.get(METHOD_TAG);
            if (tag == null) {
                tag = name.replaceAll("/", ".");
            }
            methodTag.visitLdcInsn(String.valueOf(tag));
            methodTag.visitInsn(ARETURN);
            methodTag.visitEnd();
        }
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
