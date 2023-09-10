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

import chao.android.gradle.servicepool.hunter.asm.ExtendClassWriter;


/**
 *
 * @author luqin
 * @since 2019-07-15
 */
public class AutoServiceAnnotationDetect extends ClassVisitor implements Constant {

    private List<Object> typeServiceAnnotation;

    private Map<String, AutoServiceField> fieldServiceAnnotations;

    private Map<String, AutoServiceField> fieldEventAnnotations;

    /**
     *  标记当前static的Field是否被@Service标记
     */
    private boolean hasStaticField;

    /**
     *  标记当前class是否是一个Event
     */
    private boolean hasEventAnnotation;

    private List<String> eventInterfaces;

    private List<Object> typeInitAnnotation;

    private String className;

    private String superName;

    private String[] interfaces;

    private boolean inherited;

    private ExtendClassWriter ecw;

    public AutoServiceAnnotationDetect(ExtendClassWriter ecw) {
        super(ASM7, null);
        fieldServiceAnnotations = new HashMap<>();
        fieldEventAnnotations = new HashMap<>();
        eventInterfaces = new ArrayList<>();
        this.ecw = ecw;
    }

    public AutoServiceAnnotationDetect(String className, AutoServiceAnnotationDetect copy) {
        super(ASM7, null);
        this.className = className;
        this.hasStaticField = copy.hasStaticField;
        this.hasEventAnnotation = copy.hasEventAnnotation;
        this.eventInterfaces = new ArrayList<>(copy.eventInterfaces);
        this.fieldEventAnnotations = new HashMap<>(copy.fieldEventAnnotations);
        this.fieldServiceAnnotations = new HashMap<>(copy.fieldServiceAnnotations);

        this.typeServiceAnnotation = new ArrayList<>(copy.typeServiceAnnotation);
//        this.typeInitAnnotation = new ArrayList<>(copy.typeInitAnnotation);
        this.inherited = copy.inherited;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
        this.superName = superName;
        this.interfaces = interfaces;
        if (interfaces == null) {
            return;
        }
        //查找接口是否包含@Event注解
        for (String itf : interfaces) {
            if (ecw.typeHasAnnotation(itf.replaceAll("/", "."), Constant.EVENT_ANNOTATION)) {
                if (!eventInterfaces.contains(itf)) {
                    eventInterfaces.add(itf);
                }
            }
        }
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
        } else if (EVENT_DESC.equals(desc)) {
            hasEventAnnotation = true;
        } else if (SERVICES_DESC.equals(desc)) {
            typeServiceAnnotation = new ArrayList<>();
            return new ServiceAnnotationsValueDetect(av);
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

        return new AutoServiceAnnotationFieldDetect(fv, field);
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
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

    public Map<String, AutoServiceField> getFieldEventAnnotations() {
        return fieldEventAnnotations;
    }

    public boolean isHasEventAnnotation() {
        return hasEventAnnotation;
    }

    public List<String> getEventInterfaces() {
        return eventInterfaces;
    }

    public boolean isHasStaticField() {
        return hasStaticField;
    }

    public String getClassName() {
        return className;
    }

    private class ServiceAnnotationValueDetect extends AnnotationVisitor {

        public ServiceAnnotationValueDetect(AnnotationVisitor av) {
            super(ASM7, av);
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            if (typeServiceAnnotation != null) {
                typeServiceAnnotation.add(name);
                typeServiceAnnotation.add(value);
            }

            if ("inherited".equals(name) && Boolean.parseBoolean(String.valueOf(value))) {
                inherited = true;
            }
        }
    }

    private class ServiceAnnotationsValueDetect extends AnnotationVisitor {

        public ServiceAnnotationsValueDetect(AnnotationVisitor av) {
            super(ASM7, av);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            AnnotationVisitor av = super.visitArray(name);
            return new ServiceAnnotationsAnnotationDetect(av);
        }

        private class ServiceAnnotationsAnnotationDetect extends AnnotationVisitor {
            public ServiceAnnotationsAnnotationDetect(AnnotationVisitor av) {
                super(ASM7, av);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String name, String desc) {
                return new ServiceAnnotationValueDetect(super.visitAnnotation(name, desc));
            }
        }
    }

    private class InitAnnotationValueDetect extends AnnotationVisitor {

        private List<Type> dependencies;

        public InitAnnotationValueDetect(AnnotationVisitor av) {
            super(ASM7, av);
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
                super(ASM7, av);
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
            super(ASM7, fv);
            this.field = field;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            AnnotationVisitor av = super.visitAnnotation(desc, visible);
            if (SERVICE_DESC.equals(desc)) {
                String uniqueKey = field.name + field + desc;
                fieldServiceAnnotations.put(uniqueKey, field);
                if (field.isStatic) {
                    hasStaticField = true;
                }
                return new AutoServiceFieldAnnotationDetect(av);
            } else if (EVENT_DESC.equals(desc)) {
                String uniqueKey = field.name + field + desc;
                fieldEventAnnotations.put(uniqueKey, field);
            }
            return av;
        }

        /**
         *  抓取Field域的Service注解上的值(value)
         */
        private class AutoServiceFieldAnnotationDetect extends AnnotationVisitor {

            public AutoServiceFieldAnnotationDetect(AnnotationVisitor av) {
                super(ASM7, av);
            }

            @Override
            public void visit(String name, Object value) {
                super.visit(name, value);
                //注解Annotation的方法没有明确指定返回值时，这里并不会被回调，更不会返回default值
                if (METHOD_VALUE.equals(name)) {
                    Type valueType = (Type) value;
                    field.annotationValue = valueType.getDescriptor();
                } else if (METHOD_DISABLE_INTERCEPT.equals(name)) {
                    field.annotationPath = String.valueOf(value);
                }
            }
        }

    }

}
