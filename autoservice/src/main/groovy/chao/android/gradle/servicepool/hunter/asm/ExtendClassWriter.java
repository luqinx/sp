package chao.android.gradle.servicepool.hunter.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chao.android.gradle.servicepool.compiler.Constant;


/**
 * Created by quinn on 30/08/2018
 */
public class ExtendClassWriter extends ClassWriter {

    public static final String TAG = "ExtendClassWriter";

    public static final String OBJECT = "java/lang/Object";

    private ClassLoader urlClassLoader;

    public ExtendClassWriter(ClassLoader urlClassLoader, int flags) {
        super(flags);
        this.urlClassLoader = urlClassLoader;
    }

    /**
     * https://github.com/Moniter123/pinpoint/blob/40106ffe6cc4d6aea9d59b4fb7324bcc009483ee/profiler/src/main/java/com/navercorp/pinpoint/profiler/instrument/ASMClassWriter.java
     */
    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        if (type1 == null || type1.equals(OBJECT) || type2 == null || type2.equals(OBJECT)) {
            return OBJECT;
        }

        if (type1.equals(type2)) {
            return type1;
        }

        ClassReader type1ClassReader = getClassReader(type1);
        ClassReader type2ClassReader = getClassReader(type2);
        if (type1ClassReader == null || type2ClassReader == null) {
            return OBJECT;
        }

        if (isInterface(type1ClassReader)) {
            String interfaceName = type1;
            if (isImplements(interfaceName, type2ClassReader)) {
                return interfaceName;
            }
            if (isInterface(type2ClassReader)) {
                interfaceName = type2;
                if (isImplements(interfaceName, type1ClassReader)) {
                    return interfaceName;
                }
            }
            return OBJECT;
        }

        if (isInterface(type2ClassReader)) {
            String interfaceName = type2;
            if (isImplements(interfaceName, type1ClassReader)) {
                return interfaceName;
            }
            return OBJECT;
        }

        final Set<String> superClassNames = new HashSet<String>();
        superClassNames.add(type1);
        superClassNames.add(type2);

        String type1SuperClassName = type1ClassReader.getSuperName();
        if (!superClassNames.add(type1SuperClassName)) {
            return type1SuperClassName;
        }

        String type2SuperClassName = type2ClassReader.getSuperName();
        if (!superClassNames.add(type2SuperClassName)) {
            return type2SuperClassName;
        }

        while (type1SuperClassName != null || type2SuperClassName != null) {
            if (type1SuperClassName != null) {
                type1SuperClassName = getSuperClassName(type1SuperClassName);
                if (type1SuperClassName != null) {
                    if (!superClassNames.add(type1SuperClassName)) {
                        return type1SuperClassName;
                    }
                }
            }

            if (type2SuperClassName != null) {
                type2SuperClassName = getSuperClassName(type2SuperClassName);
                if (type2SuperClassName != null) {
                    if (!superClassNames.add(type2SuperClassName)) {
                        return type2SuperClassName;
                    }
                }
            }
        }

        return OBJECT;
    }

    public boolean isImplements(final String interfaceName, final ClassReader classReader) {
//        System.out.println("" + interfaceName + " implementation " + classReader.getClassName());
        ClassReader classInfo = classReader;

        if (classReader.getClassName().equals(Constant.SERVICE_ASM_NAME)) {
            return false;
        }

        while (classInfo != null) {
            final String[] interfaceNames = classInfo.getInterfaces();
            for (String name : interfaceNames) {
                if (name != null && name.equals(interfaceName)) {
                    return true;
                }
            }

            for (String name : interfaceNames) {
                if(name != null) {
                    final ClassReader interfaceInfo = getClassReader(name);
                    if (interfaceInfo != null) {
                        if (isImplements(interfaceName, interfaceInfo)) {
                            return true;
                        }
                    }
                }
            }

            final String superClassName = classInfo.getSuperName();
            if (superClassName == null || superClassName.equals(OBJECT)) {
                break;
            }
            classInfo = getClassReader(superClassName);
        }
        return false;
    }

    private boolean isInterface(final ClassReader classReader) {
        return (classReader.getAccess() & Opcodes.ACC_INTERFACE) != 0;
    }

    public List<String> getInterfaces(String className) {
        ClassReader classReader = getClassReader(className);
        if (classReader != null) {
            return Arrays.asList(classReader.getInterfaces());
        }
        return Collections.emptyList();
    }

    public List<String> getSuperNames(String className) {
        List<String> superList = new ArrayList<>();
        if (className == null) {
            return superList;
        }
        while (className != null && !className.equals(OBJECT)) {
            superList.add(className);
            className = getSuperClassName(className);
        }
        List<String> interfaces = new ArrayList<>();
        for (String name: superList) {
            ClassReader classReader = getClassReader(name);
            if (classReader != null) {
                interfaces.addAll(Arrays.asList(classReader.getInterfaces()));
            }
        }
        superList.addAll(interfaces);
        return superList;
    }

    private String getSuperClassName(final String className) {
        final ClassReader classReader = getClassReader(className);
        if (classReader == null) {
            return null;
        }
        return classReader.getSuperName();
    }

    public ClassReader getClassReader(final String className) {
        InputStream inputStream = urlClassLoader.getResourceAsStream(className + ".class");
        try {
            if (inputStream != null) {
                return new ClassReader(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    public InputStream getInputStream(String className) {
        return urlClassLoader.getResourceAsStream(className + ".class");
    }

    public boolean typeHasAnnotation(String itf, String ant) {
        try {
//            logger.log("load class: " + itf);
            Class clazz = urlClassLoader.loadClass(itf);
            Annotation[] annotations = clazz.getAnnotations();
            for (Annotation annotation: annotations) {
//                logger.log(annotation.annotationType().getName());
                if (annotation.annotationType().getName().equals(ant)) {
                    return true;
                }
            }
        } catch (Throwable e) {
//            e.printStackTrace();
        }
        return false;
    }
}

