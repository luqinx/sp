package chao.android.gradle.servicepool.compiler;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

import chao.android.gradle.plugin.util.logger;
import chao.android.gradle.servicepool.Logger;

/**
 *
 * @author luqin
 * @since  2019-07-15
 */
public class AutoServiceFieldClassVisitor extends ClassVisitor implements Constant {

    private Map<String, AutoServiceField> fields;

    private String owner;

    private boolean clinitProcessed = false;

    public AutoServiceFieldClassVisitor(ClassVisitor classVisitor, Map<String,AutoServiceField> fields) {
        super(Opcodes.ASM6, classVisitor);
        this.fields = fields;
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//        logger.log(name + ", " + desc + ", " + signature);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ("<init>".equals(name)) {
            return new InitMethodVisitor(mv);
        } else if ("<clinit>".equals(name)) {
            clinitProcessed = true;
            return new ClinitMethodVisitor(mv);
        }
        return mv;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
//        logger.log(name + ", " + desc + ", " + signature + ", " + value);
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public void visitEnd() {
        logger.log(owner);
        if (!clinitProcessed) {
            logger.log("proceed..." + owner);
            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            for (AutoServiceField field: fields.values()) {
                if (!field.isStatic) {
                    continue;
                }
                mv.visitVarInsn(ALOAD, 0);
                if (field.annotationValue == null) {
                    field.annotationValue = field.desc;
                }
                mv.visitLdcInsn(Type.getType(field.annotationValue));
                mv.visitMethodInsn(INVOKESTATIC, "chao/java/tools/servicepool/ServicePool", "getService", "(Ljava/lang/Class;)Ljava/lang/Object;", false);
                mv.visitTypeInsn(CHECKCAST, field.asmFullName);
                mv.visitFieldInsn(PUTSTATIC, AutoServiceFieldClassVisitor.this.owner, field.name, field.desc);
            }
        }
        super.visitEnd();
    }

    private class InitMethodVisitor extends MethodVisitor {


        public InitMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM6, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);

            for (AutoServiceField field: fields.values()) {
                if (field.isStatic) {
                    continue;
                }
                mv.visitVarInsn(ALOAD, 0);
                if (field.annotationValue == null) {
                    field.annotationValue = field.desc;
                }
                mv.visitLdcInsn(Type.getType(field.annotationValue));
                mv.visitMethodInsn(INVOKESTATIC, "chao/java/tools/servicepool/ServicePool", "getService", "(Ljava/lang/Class;)Ljava/lang/Object;", false);
                mv.visitTypeInsn(CHECKCAST, field.asmFullName);
                mv.visitFieldInsn(PUTFIELD, AutoServiceFieldClassVisitor.this.owner, field.name, field.desc);
            }
        }

    }

    private class ClinitMethodVisitor extends MethodVisitor {


        public ClinitMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM6, mv);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            for (AutoServiceField field: fields.values()) {
                if (!field.isStatic) {
                    continue;
                }
                mv.visitVarInsn(ALOAD, 0);
                if (field.annotationValue == null) {
                    field.annotationValue = field.desc;
                }
                mv.visitLdcInsn(Type.getType(field.annotationValue));
                mv.visitMethodInsn(INVOKESTATIC, "chao/java/tools/servicepool/ServicePool", "getService", "(Ljava/lang/Class;)Ljava/lang/Object;", false);
                mv.visitTypeInsn(CHECKCAST, field.asmFullName);
                mv.visitFieldInsn(PUTSTATIC, AutoServiceFieldClassVisitor.this.owner, field.name, field.desc);
            }
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
        }
    }
}
