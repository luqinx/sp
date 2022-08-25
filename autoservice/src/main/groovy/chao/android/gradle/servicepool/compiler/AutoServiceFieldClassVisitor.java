package chao.android.gradle.servicepool.compiler;

import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 *
 * @author luqin
 * @since  2019-07-15
 */
public class AutoServiceFieldClassVisitor extends ClassVisitor implements Constant {

    private Map<String, AutoServiceField> fields;

    private Map<String, AutoServiceField> events;

    private List<String> eventInterfaces;

    private boolean hasStaticField;

    private String owner;

    private String superName;

    private boolean clinitProcessed = false;

    public AutoServiceFieldClassVisitor(ClassVisitor classVisitor, AutoServiceAnnotationDetect detect) {
        super(Opcodes.ASM6, classVisitor);
        this.fields = detect.getFieldServiceAnnotations();
        this.hasStaticField = detect.isHasStaticField();
        this.eventInterfaces = detect.getEventInterfaces();
        this.events = detect.getFieldEventAnnotations();
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
        this.superName = superName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//        logger.log(name + ", " + desc + ", " + signature);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ("<init>".equals(name)) {
//            logger.log(owner + ", " + name + ", " + desc + ", " + signature);
            return new InitMethodVisitor(mv);
        } else if ("<clinit>".equals(name) && hasStaticField) {
            clinitProcessed = true;
            return new ClinitMethodVisitor(mv);
        }
        return mv;
    }


    /**
     *  "clinit"不存在， 则创建"clinit"方法，并追加service赋值代码
     */
    @Override
    public void visitEnd() {
        if (!clinitProcessed && hasStaticField) {
//            System.out.println("generate " + owner + " clinit method.");
            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            for (AutoServiceField field: fields.values()) {
                if (!field.isStatic) {
                    continue;
                }
//                System.out.println("generate static field in gen clinit method: " + owner + "#" + field);

                //如果path存在, 使用path查找
                if (field.annotationPath != null && field.annotationPath.length() > 0) {
                    mv.visitLdcInsn(field.annotationPath);
                    mv.visitMethodInsn(INVOKESTATIC, "chao/java/tools/servicepool/ServicePool", "getService", "(Ljava/lang/String;)Lchao/java/tools/servicepool/IService;", false);
                    mv.visitTypeInsn(CHECKCAST, field.asmFullName);
                    mv.visitFieldInsn(PUTSTATIC, AutoServiceFieldClassVisitor.this.owner, field.name, field.desc);
                } else {
                    if (field.annotationValue == null) {
                        field.annotationValue = field.desc;
                    }
                    mv.visitLdcInsn(Type.getType(field.annotationValue));
                    mv.visitMethodInsn(INVOKESTATIC, "chao/java/tools/servicepool/ServicePool", "getService", "(Ljava/lang/Class;)Lchao/java/tools/servicepool/IService;", false);
                    mv.visitTypeInsn(CHECKCAST, field.asmFullName);
                    mv.visitFieldInsn(PUTSTATIC, AutoServiceFieldClassVisitor.this.owner, field.name, field.desc);
                }
            }
            mv.visitInsn(RETURN);
            mv.visitEnd();
        }
        super.visitEnd();
    }

    private class InitMethodVisitor extends MethodVisitor {


        public InitMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM6, mv);
        }

        @Override
        public void visitCode() {
//            System.out.println(owner + " visitCode");
            super.visitCode();
        }

        @Override
        public void visitEnd() {
//            System.out.println(owner + " visitEnd");
            super.visitEnd();
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            super.visitTypeInsn(opcode, type);
//            System.out.println(owner + " visitTypeInsn : " + type + " " + opcode);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
//            System.out.println(owner + "." + name + "(" +desc + ") visitMethodInsn: " + opcode);
//            System.out.println(superName);
            if (opcode == Opcodes.INVOKESPECIAL
                    && owner.equals(superName)
                    && "<init>".equals(name)) { //父类构造方法
                generateGetServiceCodeInInitMethod();
            }
        }

        private void generateGetServiceCodeInInitMethod() {
//            System.out.println(owner + " init method.");

            //初始化Service, 调用ServicePool.getService()给成员变量Service赋值
            for (AutoServiceField field : events.values()) {
//                if (field.isStatic) {
//                    continue;
//                }
                mv.visitVarInsn(ALOAD, 0);
                mv.visitLdcInsn(Type.getType(field.desc));
                mv.visitMethodInsn(INVOKESTATIC, "chao/java/tools/servicepool/ServicePool", "getEventService", "(Ljava/lang/Class;)Lchao/java/tools/servicepool/event/EventService;", false);
                mv.visitTypeInsn(CHECKCAST, field.asmFullName);
                mv.visitFieldInsn(PUTFIELD, AutoServiceFieldClassVisitor.this.owner, field.name, field.desc);
            }

            //初始化Event, 调用ServicePool.getEventService()给成员变量Service赋值
            for (AutoServiceField field : fields.values()) {
                if (field.isStatic) {
                    continue;
                }

//                System.out.println("generate field in init method: " + owner + "#" + field);

                mv.visitVarInsn(ALOAD, 0);

                //如果path存在, 使用path查找
                if (field.annotationPath != null && field.annotationPath.length() > 0) {
                    mv.visitLdcInsn(field.annotationPath);
                    mv.visitMethodInsn(INVOKESTATIC, "chao/java/tools/servicepool/ServicePool", "getService", "(Ljava/lang/String;)Lchao/java/tools/servicepool/IService;", false);
                    mv.visitTypeInsn(CHECKCAST, field.asmFullName);
                    mv.visitFieldInsn(PUTFIELD, AutoServiceFieldClassVisitor.this.owner, field.name, field.desc);
                } else {
                    if (field.annotationValue == null) {
                        field.annotationValue = field.desc;
                    }
                    mv.visitLdcInsn(Type.getType(field.annotationValue));
                    mv.visitMethodInsn(INVOKESTATIC, "chao/java/tools/servicepool/ServicePool", "getService", "(Ljava/lang/Class;)Lchao/java/tools/servicepool/IService;", false);
                    mv.visitTypeInsn(CHECKCAST, field.asmFullName);
                    mv.visitFieldInsn(PUTFIELD, AutoServiceFieldClassVisitor.this.owner, field.name, field.desc);
                }
            }

            //初始化Event, 注册Event
            if (eventInterfaces.size() > 0) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, "chao/java/tools/servicepool/ServicePool", "registerEventService", "(Lchao/java/tools/servicepool/event/EventService;)V", false);
            }
        }
    }

    /**
     * 已存在<clinit>, 追加service赋值代码
     */
    private class ClinitMethodVisitor extends MethodVisitor {


        public ClinitMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM6, mv);
        }

        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
//                System.out.println(owner + " exists clinit method.");

                for (AutoServiceField field : fields.values()) {
                    if (!field.isStatic) {
                        continue;
                    }
//                    System.out.println("generate static field in exists clinit method: " + owner + "#" + field);
                    if (field.annotationValue == null) {
                        field.annotationValue = field.desc;
                    }
                    mv.visitLdcInsn(Type.getType(field.annotationValue));
                    mv.visitMethodInsn(INVOKESTATIC, "chao/java/tools/servicepool/ServicePool", "getService", "(Ljava/lang/Class;)Lchao/java/tools/servicepool/IService;", false);
                    mv.visitTypeInsn(CHECKCAST, field.asmFullName);
                    mv.visitFieldInsn(PUTSTATIC, AutoServiceFieldClassVisitor.this.owner, field.name, field.desc);
                }
//                mv.visitEnd();
            }
            super.visitInsn(opcode);
        }
    }
}
