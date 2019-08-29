package chao.android.gradle.servicepool.compiler;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author luqin
 * @since 2019-07-15
 */
public class AutoServiceAnnotationDetect extends ClassVisitor implements Constant {

    private List<Object> typeServiceAnnotation;

    private Map<String, AutoServiceField> fieldServiceAnnotations;

    private boolean hasStaticField;


    private List<Object> typeInitAnnotation;

    private String className;

    private String superName;

    private String[] interfaces;

    public AutoServiceAnnotationDetect(ClassVisitor cv) {
        super(ASM6, cv);
        fieldServiceAnnotations = new HashMap<>();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
        this.superName = superName;
        this.interfaces = interfaces;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(desc, visible);
//        System.out.println("visitAnnotation: " + desc);
        if (SERVICE_DESC.equals(desc)) {
            typeServiceAnnotation = new ArrayList<>();
            return new ServiceAnnotationValueDetect(av);
        } else if (INIT_DESC.equals(desc)) {
            typeInitAnnotation = new ArrayList<>();
            return new InitAnnotationValueDetect(av);
        }
        return av;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldVisitor fv = super.visitField(access, name, desc, signature, value);
        AutoServiceField field = new AutoServiceField();
        field.name = name;
        field.desc = desc;
        if (desc != null && desc.length() > 2) {
            field.asmFullName = desc.substring(1, desc.length() - 1);
        }
        field.signature = signature;
        field.value = value;
        field.isStatic = (access & Opcodes.ACC_STATIC) != 0;
        if (field.isStatic) {
            hasStaticField = true;
        }

        return new AutoServiceAnnotationFieldDetect(fv, field);
    }

    public List<Object> getTypeServiceAnnotation() {
        return typeServiceAnnotation;
    }

    public List<Object> getTypeInitAnnotation() {
        return typeInitAnnotation;
    }

    public Map<String, AutoServiceField> getFieldServiceAnnotations() {
        return fieldServiceAnnotations;
    }

    public boolean isHasStaticField() {
        return hasStaticField;
    }

    public String getClassName() {
        return className;
    }

    private class ServiceAnnotationValueDetect extends AnnotationVisitor {

        public ServiceAnnotationValueDetect(AnnotationVisitor av) {
            super(ASM6, av);
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            if (typeServiceAnnotation != null) {
                typeServiceAnnotation.add(name);
                typeServiceAnnotation.add(value);
            }
        }

    }

    private class InitAnnotationValueDetect extends AnnotationVisitor {

        private List<Type> dependencies;

        public InitAnnotationValueDetect(AnnotationVisitor av) {
            super(ASM6, av);
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            if (typeInitAnnotation != null) {
                typeInitAnnotation.add(name);
                typeInitAnnotation.add(value);
            }
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            AnnotationVisitor av = super.visitArray(name);
            if (METHOD_DEPENDENCIES.equals(name)) {
                dependencies = new ArrayList<>();
                typeInitAnnotation.add(name);
                typeInitAnnotation.add(dependencies);
                return new InitArrayAnnotationDetect(av);
            }
            return av;
        }

        private class InitArrayAnnotationDetect extends AnnotationVisitor {

            public InitArrayAnnotationDetect(AnnotationVisitor av) {
                super(ASM6, av);
            }

            @Override
            public void visit(String name, Object value) {
                super.visit(name, value);
                dependencies.add((Type) value);
            }
        }
    }

    private class AutoServiceAnnotationFieldDetect extends FieldVisitor {

        private AutoServiceField field;

        public AutoServiceAnnotationFieldDetect(FieldVisitor fv, AutoServiceField field) {
            super(ASM6, fv);
            this.field = field;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            AnnotationVisitor av = super.visitAnnotation(desc, visible);
            if (SERVICE_DESC.equals(desc)) {
                String uniqueKey = field.name + field + desc;
                fieldServiceAnnotations.put(uniqueKey, field);
                return new AutoServiceFieldAnnotationDetect(av);
            }
            return av;
        }

        /**
         *  抓取Field域的Service注解上的值(value)
         */
        private class AutoServiceFieldAnnotationDetect extends AnnotationVisitor {

            public AutoServiceFieldAnnotationDetect(AnnotationVisitor av) {
                super(ASM6, av);
            }

            @Override
            public void visit(String name, Object value) {
                super.visit(name, value);
                //注解Annotation的方法没有明确指定返回值时，这里并不会被回调，更不会返回default值
                if (METHOD_VALUE.equals(name)) {
                    Type valueType = (Type) value;
                    field.annotationValue = valueType.getDescriptor();
                }
            }
        }

    }


}
