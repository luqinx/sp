package chao.android.gradle.servicepool.compiler;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chao.android.gradle.servicepool.Logger;

/**
 *
 * @author luqin
 * @since 2019-07-15
 */
public class AutoServiceAnnotationDetect extends ClassVisitor implements Constant {

    private List<Object> typeServiceAnnotation;

    private Map<String, AutoServiceField> fieldServiceAnnotations;

    private String className;

    public AutoServiceAnnotationDetect(ClassVisitor cv) {
        super(ASM6, cv);
        fieldServiceAnnotations = new HashMap<>();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(desc, visible);
        if (SERVICE_DESC.equals(desc)) {
            typeServiceAnnotation = new ArrayList<>();
            return new AutoServiceAnnotationValueDetect(av);
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

        return new AutoServiceAnnotationFieldDetect(fv, field);
    }

    public List<Object> getTypeServiceAnnotation() {
        return typeServiceAnnotation;
    }

    public Map<String, AutoServiceField> getFieldServiceAnnotations() {
        return fieldServiceAnnotations;
    }

    public String getClassName() {
        return className;
    }

    private class AutoServiceAnnotationValueDetect extends AnnotationVisitor {

        public AutoServiceAnnotationValueDetect(AnnotationVisitor av) {
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

    private class AutoServiceAnnotationFieldDetect extends FieldVisitor {

        private AutoServiceField field;

        public AutoServiceAnnotationFieldDetect(FieldVisitor fv, AutoServiceField field) {
            super(ASM6, fv);
            this.field = field;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (SERVICE_DESC.equals(desc)) {
                String uniqueKey = field.name + field + desc;
                fieldServiceAnnotations.put(uniqueKey, field);
            }
            return super.visitAnnotation(desc, visible);
        }

    }
}
