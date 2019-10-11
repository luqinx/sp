package chao.android.gradle.servicepool.compiler

import chao.android.gradle.servicepool.hunter.asm.BaseWeaver
import chao.android.gradle.servicepool.hunter.asm.ExtendClassWriter
import chao.java.tools.servicepool.IService
import org.apache.commons.io.IOUtils
import org.objectweb.asm.*

import java.nio.file.Files
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 *
 *  @author luqin
 *  @since 2019-07-09
 */
class AutoServiceWeaver extends BaseWeaver {

    private Map<String, List<ServiceInfo>> serviceInfoMap = new HashMap<>()

    private AutoServiceExtension extension

    @Override
    protected void weaveJarStarted(int jarId) {
        super.weaveJarStarted(jarId)
    }

    @Override
    byte[] weaveSingleClassToByteArray(int jarId, InputStream inputStream) throws IOException {
        ClassReader classReader = new ClassReader(inputStream)
        if (classReader.interfaces.contains(IService.class.getName())) {
            return IOUtils.toByteArray(inputStream)
        }
        ExtendClassWriter classWriter = new ExtendClassWriter(classLoader, ClassWriter.COMPUTE_MAXS)
        ClassVisitor visitor = classWriter

//        ClassNode classNode = new ClassNode()
        AutoServiceAnnotationDetect detect = new AutoServiceAnnotationDetect(classWriter)
        classReader.accept(detect, 0)
        //RetentionPolicy.RUNTIME是可见， 其他为不可见
        if (detect.typeServiceAnnotation != null) {
            visitor = collectServiceInfo(classWriter, detect)
        }

        if (detect.hasEventAnnotation) {
            visitor = new AutoServiceEventVisitor(classWriter)
        }

//        logger.log(serviceInfoMap)
        if(detect.fieldServiceAnnotations.size() > 0 || detect.fieldEventAnnotations.size() > 0) {
            visitor = new AutoServiceFieldClassVisitor(visitor, detect)
        }

        classReader.accept(visitor, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

    private ClassVisitor collectServiceInfo(ExtendClassWriter classWriter, AutoServiceAnnotationDetect classNode) {
//        logger.log(classNode.className)
        //因为使用ServicePool.getService(xxx)获取时的可能是xxx的任意子类，所以这里注册所有子类
        List<String> classes = classWriter.getSuperNames(classNode.className)
//        List<String> classes = Collections.singletonList(classNode.className)
        ServiceInfo serviceInfo = new ServiceInfo(classNode.className)
        serviceInfo.parse(classNode.typeServiceAnnotation)
        serviceInfo.parse(classNode.typeInitAnnotation)
        if (classNode.typeInitAnnotation != null) {
            serviceInfo.setInit(true)
        } else {
            //默认是懒加载
//            serviceInfo.setLazy(true)
        }

        for (String clazz: classes) {
            if (IService.class.name.replaceAll("\\.", "/") == clazz) {
                continue
            }
            clazz = clazz.replaceAll("/", ".")
            int last = clazz.lastIndexOf('.')
            String pkgName = last == -1 ? clazz : clazz.substring(0, last)

            List<ServiceInfo> infos = serviceInfoMap.get(pkgName)
            if (infos == null) {
                infos = new ArrayList<>()
                serviceInfoMap.put(pkgName, infos)
            }
            if (!infos.contains(serviceInfo)) {
                infos.add(serviceInfo)
            }
        }
        return new AutoServiceVisitor(classWriter)
    }

    @Override
    boolean isWeavableClass(String fullQualifiedClassName) {
        return super.isWeavableClass(fullQualifiedClassName)
    }

    @Override
    protected void weaveJarFinished(int jarId, ZipFile inputZip, ZipOutputStream outputZip) {
        super.weaveJarFinished(jarId, inputZip, outputZip)
    }

    private static void writeZipEntry(String entryName, byte[] content, ZipOutputStream outputZip) {
        ZipEntry zipEntry = new ZipEntry(entryName)
        CRC32 crc32 = new CRC32()
        crc32.update(content)
        zipEntry.setCrc(crc32.getValue())
        zipEntry.setMethod(ZipEntry.STORED)
        zipEntry.setSize(content.length)
        zipEntry.setCompressedSize(content.length)
        zipEntry.setLastAccessTime(ZERO)
        zipEntry.setLastModifiedTime(ZERO)
        zipEntry.setCreationTime(ZERO)
        outputZip.putNextEntry(zipEntry)
        outputZip.write(content)
        outputZip.closeEntry()
    }

    void transformFinished(File destJar) {

        ZipOutputStream outputZip = new ZipOutputStream(new BufferedOutputStream(
                Files.newOutputStream(destJar.toPath())))

        try {
            //生成 ServiceFactoriesInstance
            writeGenerateServiceFactories(outputZip)

            //生成 InitServicesInstance
            writeGenerateInitServices(outputZip)
            
            //生成 PathServicesInstance
            writeGeneratePathServices(outputZip)


            for (String pkgName : serviceInfoMap.keySet()) {
                List<ServiceInfo> infoList = serviceInfoMap.get(pkgName)
                if (infoList == null) {
                    println(pkgName + ":" + infoList)
                }

                writeGenerateFactories(outputZip, pkgName, infoList)
            }
        } finally {
            outputZip.flush()
            outputZip.close()
        }

    }

    private void writeGenerateInitServices(ZipOutputStream outputZip) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES)

        classWriter.visit(Opcodes.ASM6, Opcodes.ACC_PUBLIC, Constant.GENERATE_INIT_SERVICE_INSTANCE_ASM_NAME, null, Constant.SERVICE_INIT_SERVICES_ASM_NAME)
        classWriter.visitSource(Constant.GENERATE_INIT_SERVICE_INSTANCE_ASM_NAME, null)

        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null)
        methodVisitor.visitCode()
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Constant.SERVICE_INIT_SERVICES_ASM_NAME, "<init>", "()V", false)

        HashSet<String> descriptors = new HashSet<>()
        for (String pkgName : serviceInfoMap.keySet()) {
            List<ServiceInfo> serviceInfoList = serviceInfoMap.get(pkgName)
            for (ServiceInfo serviceInfo: serviceInfoList) {
                if (!serviceInfo.isInit() || serviceInfo.isLazy()) {
                    continue
                }
                descriptors.add(serviceInfo.descriptor)
            }
        }
        descriptors.each { descriptor->
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitLdcInsn(Type.getType(descriptor))
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Constant.GENERATE_INIT_SERVICE_INSTANCE_ASM_NAME, "addInitService", "(Ljava/lang/Class;)V", false)
        }
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitMaxs(4, 1)
        methodVisitor.visitEnd()
        classWriter.visitEnd()

        writeZipEntry(Constant.GENERATE_INIT_SERVICE_INSTANCE_ASM_NAME + Constant.GENERATE_FILE_NAME_SUFFIX, classWriter.toByteArray(), outputZip)

    }

    private void writeGeneratePathServices(ZipOutputStream outputZip) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES)

        classWriter.visit(Opcodes.ASM6, Opcodes.ACC_PUBLIC, Constant.GENERATE_PATH_SERVICE_INSTANCE_ASM_NAME, null, Constant.SERVICE_PATH_SERVICES_ASM_NAME)
        classWriter.visitSource(Constant.GENERATE_PATH_SERVICE_INSTANCE_ASM_NAME, null)

        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null)
        methodVisitor.visitCode()
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Constant.SERVICE_PATH_SERVICES_ASM_NAME, "<init>", "()V", false)

        Map<String, String> pathMap = new HashMap<>()
        for (String pkgName : serviceInfoMap.keySet()) {
            List<ServiceInfo> serviceInfoList = serviceInfoMap.get(pkgName)
            for (ServiceInfo serviceInfo: serviceInfoList) {
                if (serviceInfo.path == null || serviceInfo.path.length() == 0) {
                    continue
                }
                String path = serviceInfo.getPath()
                String descriptor = serviceInfo.getDescriptor()
                if (pathMap.get(path) != null && pathMap.get(path) != descriptor) {
                    throw new IllegalStateException("duplicate path: " + path + ", " + descriptor + " <--> " + pathMap.get(path)) //path重复
                }
                pathMap.put(path, descriptor)
            }
        }
        pathMap.each { path, descriptor->
//            println(path + "=" + descriptor)
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitLdcInsn(path)
            methodVisitor.visitLdcInsn(Type.getType(descriptor));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Constant.GENERATE_PATH_SERVICE_INSTANCE_ASM_NAME, "put", "(Ljava/lang/String;Ljava/lang/Class;)V", false);
        }
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitMaxs(4, 1)
        methodVisitor.visitEnd()
        classWriter.visitEnd()

        writeZipEntry(Constant.GENERATE_PATH_SERVICE_INSTANCE_ASM_NAME + Constant.GENERATE_FILE_NAME_SUFFIX, classWriter.toByteArray(), outputZip)

    }

    private void writeGenerateServiceFactories(ZipOutputStream outputZip) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES)

        classWriter.visit(Opcodes.ASM6, Opcodes.ACC_PUBLIC, Constant.GENERATE_SERVICE_FACTORIES_INSTANCE_ASM_NAME, null, Constant.SERVICE_FACTORIES_ASM_NAME, null)
        classWriter.visitSource(Constant.GENERATE_SERVICE_FACTORIES_INSTANCE_ASM_NAME, null)

        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null)
        methodVisitor.visitCode()
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Constant.SERVICE_FACTORIES_ASM_NAME, "<init>", "()V", false)

        for (String pkgName : serviceInfoMap.keySet()) {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitLdcInsn(pkgName)
            String serviceFactory = pkgName.replaceAll("\\.", "/") + "/" + Constant.GENERATE_SERVICE_SUFFIX
            methodVisitor.visitTypeInsn(Opcodes.NEW, serviceFactory)
            methodVisitor.visitInsn(Opcodes.DUP)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, serviceFactory, "<init>", "()V", false)
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Constant.GENERATE_SERVICE_FACTORIES_INSTANCE_ASM_NAME, "addFactory", "(Ljava/lang/String;Lchao/java/tools/servicepool/IServiceFactory;)V", false)
        }
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitMaxs(4, 1)
        methodVisitor.visitEnd()
        classWriter.visitEnd()

        writeZipEntry(Constant.GENERATE_SERVICE_FACTORIES_INSTANCE_ASM_NAME + Constant.GENERATE_FILE_NAME_SUFFIX, classWriter.toByteArray(), outputZip)
    }

    /**
     * 自动生成方法 xxx_ServiceFactory#createServiceProxy
     * @param classWriter
     * @param infoList
     */
    private static void generateCreateServiceProxy(ClassWriter classWriter, List<ServiceInfo> infoList) {
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "createServiceProxy", "(Ljava/lang/Class;)Lchao/java/tools/servicepool/ServiceProxy;", null, null)
        methodVisitor.visitCode()

        infoList.sort(new Comparator<ServiceInfo>() {
            @Override
            int compare(ServiceInfo s1, ServiceInfo s2) {
                return s2.priority - s1.priority
            }
        })


        //有限判断是否相等， 申请类名和Service实体类名一致时，是最高优先级
        for (ServiceInfo info : infoList) {
            Label li = new Label()
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
            methodVisitor.visitLdcInsn(Type.getType(info.getDescriptor()))
            methodVisitor.visitJumpInsn(Opcodes.IF_ACMPNE, li)

            methodVisitor.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList")
            methodVisitor.visitInsn(Opcodes.DUP)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false)
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 2)
            for (Type type: info.getDependencies()) {
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
                methodVisitor.visitLdcInsn(type)
                methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false)
                methodVisitor.visitInsn(Opcodes.POP)
            }
            methodVisitor.visitTypeInsn(Opcodes.NEW, "chao/java/tools/servicepool/ServiceProxy")
            methodVisitor.visitInsn(Opcodes.DUP)
            methodVisitor.visitLdcInsn(Type.getType(info.getDescriptor()))
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitInsn(info.getPriority() + 3)
            methodVisitor.visitInsn(info.getScope() + 3)
            methodVisitor.visitLdcInsn(info.getPath())
            methodVisitor.visitInsn(info.async ? Opcodes.ICONST_1: Opcodes.ICONST_0)
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
//            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "chao/java/tools/servicepool/ServiceProxy", "<init>", "(Ljava/lang/Class;Lchao/java/tools/servicepool/IServiceFactory;IILjava/lang/String;)V", false)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "chao/java/tools/servicepool/ServiceProxy", "<init>", "(Ljava/lang/Class;Lchao/java/tools/servicepool/IServiceFactory;IILjava/lang/String;ZLjava/util/List;)V", false)

            methodVisitor.visitInsn(Opcodes.ARETURN)
            methodVisitor.visitLabel(li)
        }
        //再判断
        for (ServiceInfo info : infoList) {
            Label li = new Label()

            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
            methodVisitor.visitLdcInsn(Type.getType(info.getDescriptor()))
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "isAssignableFrom", "(Ljava/lang/Class;)Z", false)
            methodVisitor.visitJumpInsn(Opcodes.IFEQ, li)

            methodVisitor.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList")
            methodVisitor.visitInsn(Opcodes.DUP)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false)
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 2)
            for (Type type: info.getDependencies()) {
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
                methodVisitor.visitLdcInsn(type)
                methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false)
                methodVisitor.visitInsn(Opcodes.POP)
            }

            methodVisitor.visitTypeInsn(Opcodes.NEW, "chao/java/tools/servicepool/ServiceProxy")
            methodVisitor.visitInsn(Opcodes.DUP)
            methodVisitor.visitLdcInsn(Type.getType(info.getDescriptor()))
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitInsn(info.getPriority() + 3)
            methodVisitor.visitInsn(info.getScope() + 3)
            methodVisitor.visitLdcInsn(info.getPath())
            methodVisitor.visitInsn(info.async ? Opcodes.ICONST_1: Opcodes.ICONST_0)
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)

//            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "chao/java/tools/servicepool/ServiceProxy", "<init>", "(Ljava/lang/Class;Lchao/java/tools/servicepool/IServiceFactory;IILjava/lang/String;)V", false)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "chao/java/tools/servicepool/ServiceProxy", "<init>", "(Ljava/lang/Class;Lchao/java/tools/servicepool/IServiceFactory;IILjava/lang/String;ZLjava/util/List;)V", false)
            methodVisitor.visitInsn(Opcodes.ARETURN)
            methodVisitor.visitLabel(li)
        }
        methodVisitor.visitInsn(Opcodes.ACONST_NULL)
        methodVisitor.visitInsn(Opcodes.ARETURN)
        methodVisitor.visitMaxs(12, 3)
        methodVisitor.visitEnd()
    }

    private static void generateCreateServiceProxies(ClassWriter classWriter, List<ServiceInfo> infoList) {
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "createServiceProxies", "(Ljava/lang/Class;)Ljava/util/HashSet;", "(Ljava/lang/Class;)Ljava/util/HashSet<Lchao/java/tools/servicepool/ServiceProxy;>;", null)
        methodVisitor.visitCode()

        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/util/HashSet")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashSet", "<init>", "()V", false)
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 3)

        for (ServiceInfo info : infoList) {
            Label li = new Label()

            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
            methodVisitor.visitLdcInsn(Type.getType(info.getDescriptor()))
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "isAssignableFrom", "(Ljava/lang/Class;)Z", false)
            methodVisitor.visitJumpInsn(Opcodes.IFEQ, li)

            methodVisitor.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList")
            methodVisitor.visitInsn(Opcodes.DUP)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false)
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 2)
            for (Type type: info.getDependencies()) {
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
                methodVisitor.visitLdcInsn(type)
                methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false)
                methodVisitor.visitInsn(Opcodes.POP)
            }

            methodVisitor.visitTypeInsn(Opcodes.NEW, "chao/java/tools/servicepool/ServiceProxy")
            methodVisitor.visitInsn(Opcodes.DUP)
            methodVisitor.visitLdcInsn(Type.getType(info.getDescriptor()))
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitInsn(info.getPriority() + 3)
            methodVisitor.visitInsn(info.getScope() + 3)
            methodVisitor.visitLdcInsn(info.getPath())
            methodVisitor.visitInsn(info.async ? Opcodes.ICONST_1: Opcodes.ICONST_0)
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)

//            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "chao/java/tools/servicepool/ServiceProxy", "<init>", "(Ljava/lang/Class;Lchao/java/tools/servicepool/IServiceFactory;IILjava/lang/String;)V", false)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "chao/java/tools/servicepool/ServiceProxy", "<init>", "(Ljava/lang/Class;Lchao/java/tools/servicepool/IServiceFactory;IILjava/lang/String;ZLjava/util/List;)V", false)
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 4)

            methodVisitor.visitVarInsn(Opcodes.ALOAD, 3)// HashSet
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 4)
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashSet", "add", "(Ljava/lang/Object;)Z", false)
            methodVisitor.visitInsn(Opcodes.POP)
            methodVisitor.visitLabel(li)
        }
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 3) //HashSet
        methodVisitor.visitInsn(Opcodes.ARETURN)
        methodVisitor.visitMaxs(12, 5)
        methodVisitor.visitEnd()
    }

    private static void generateCreateInstance(ClassWriter classWriter, List<ServiceInfo> infoList) {
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "createInstance", "(Ljava/lang/Class;)Lchao/java/tools/servicepool/IService;", "(Ljava/lang/Class<*>;)Lchao/java/tools/servicepool/IService;", null)
        methodVisitor.visitCode()

        Label goEnd = new Label()
        for (ServiceInfo info : infoList) {
            Label li = new Label()
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
            methodVisitor.visitLdcInsn(Type.getType(info.getDescriptor()))
//            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "isAssignableFrom", "(Ljava/lang/Class;)Z", false)
//            methodVisitor.visitJumpInsn(Opcodes.IFEQ, li)
            methodVisitor.visitJumpInsn(Opcodes.IF_ACMPNE, li)
            methodVisitor.visitTypeInsn(Opcodes.NEW, info.getAsmName())
            methodVisitor.visitInsn(Opcodes.DUP)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, info.getAsmName(), "<init>", "()V", false)
            methodVisitor.visitInsn(Opcodes.ARETURN)
            methodVisitor.visitLabel(li)
        }

        methodVisitor.visitLabel(goEnd)
        methodVisitor.visitInsn(Opcodes.ACONST_NULL)
        methodVisitor.visitInsn(Opcodes.ARETURN)
        methodVisitor.visitMaxs(2, 2)
        methodVisitor.visitEnd()
    }

    /**
     * 自动生成 chao/java/tools/servicepool/gen/(xxx_xxx_xxx)_ServiceFactory.class
     * xxx_xxx_xxx是被@Service注解的类的包名pkgName;
     * 同时通过 {@link #generateCreateServiceProxy}生成serviceProxy(Class)和 通过{@link #generateCreateInstance}
     * 生成createInstance(Class)两个方法
     *
     * @see #generateCreateServiceProxy
     * @param outputZip zip输出
     * @param pkgName 被@Service注解的类的包名pkgName
     * @param infoList 同pkgName下所有的类信息列表
     */
    private static void writeGenerateFactories(ZipOutputStream outputZip, String pkgName, List<ServiceInfo> infoList) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES)

        String className = pkgName.replaceAll("\\.", "/") + "/" + Constant.GENERATE_SERVICE_SUFFIX
        classWriter.visit(Opcodes.ASM6, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", Constant.SERVICE_FACTORY_ASM_NAME)

        generateCreateServiceProxy(classWriter, infoList)

        generateCreateServiceProxies(classWriter, infoList)

        generateCreateInstance(classWriter, infoList)

        classWriter.visitEnd()

        writeZipEntry(className + Constant.GENERATE_FILE_NAME_SUFFIX, classWriter.toByteArray(), outputZip)
    }

    @Override
    void setExtension(Object extension) {
        this.extension = extension
    }

    @Override
    boolean weaverJarExcluded(String jarName) {
        List<String> excludes = extension.excludes()
        for (String exclude : excludes) {
            if (jarName.startsWith(exclude)) {
                return true
            }
        }
        return false
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return super.wrapClassWriter(classWriter)
    }
}
