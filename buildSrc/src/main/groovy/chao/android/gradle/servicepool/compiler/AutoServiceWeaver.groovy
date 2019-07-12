package chao.android.gradle.servicepool.compiler


import chao.android.gradle.servicepool.hunter.asm.BaseWeaver
import chao.android.gradle.servicepool.hunter.asm.ExtendClassWriter
import chao.java.tools.servicepool.IService
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode

import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 *
 * @project: zmjx-sp
 * @description:
 *  @author luqin  qinchao@mochongsoft.com
 * @date 2019-07-09
 */
class AutoServiceWeaver extends BaseWeaver {

    private static final String SERVICE_DESC = "Lchao/java/tools/servicepool/annotation/Service;"

    private static final String SERVICES_DIRECTORY = "META-INF/services/"

    private static final String ISERVICE_NAME = IService.class.name

    private Map<Integer, List<String>> serviceConfigMap = new HashMap<>()

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
        ClassWriter classWriter = new ExtendClassWriter(classLoader, ClassWriter.COMPUTE_MAXS)
        ClassVisitor visitor = classWriter

        ClassNode classNode = new ClassNode()
        classReader.accept(classNode, 0)
        //RetentionPolicy.RUNTIME是可见， 其他为不可见
        if (classNode.invisibleAnnotations != null) {
            for (AnnotationNode node : classNode.invisibleAnnotations) {
                if (SERVICE_DESC == node.desc) {
//                    Logger.log("SERVICE_DESC == node.desc", node.desc, node.values, classNode.name.replaceAll("/", "."))
                    List<String> list = serviceConfigMap.get(jarId)
                    if (list == null) {
                        list = new ArrayList<>()
                        serviceConfigMap.put(jarId, list)
                    }
                    list.add(classNode.name.replaceAll("/", "."))

                    int count = 0
                    if (node.values != null) {
                        count = node.values.size() / 2
                    }

                    Map<String, Object> values = new HashMap<>(count)
                    for (int i = 0; i < values.length; i=i+2) {
                        values.put(node.values[i], node.values[i+1])
                    }
                    visitor = new AutoServiceVisitor(classWriter, values)
                }
            }
        }
        classReader.accept(visitor, ClassWriter.COMPUTE_FRAMES)
        return classWriter.toByteArray()
//        return super.weaveSingleClassToByteArray(jarId, inputStream)
    }

    @Override
    boolean isWeavableClass(String fullQualifiedClassName) {
        return super.isWeavableClass(fullQualifiedClassName)
    }

    @Override
    protected void weaveJarFinished(int jarId, ZipOutputStream outputZip) {
        super.weaveJarFinished(jarId, outputZip)
        List<String> services = serviceConfigMap.get(jarId)
        if (services == null) {
            return
        }
        ZipEntry parent = new ZipEntry(SERVICES_DIRECTORY)
        CRC32 crc32 = new CRC32()
        crc32.update()
        parent.setCrc(crc32.getValue())
        parent.setMethod(ZipEntry.STORED)
        parent.setSize(0)
        parent.setLastAccessTime(ZERO)
        parent.setLastModifiedTime(ZERO)
        parent.setCreationTime(ZERO)
        outputZip.putNextEntry(parent)
        outputZip.closeEntry()


        File serviceConfigFile = File.createTempFile(ISERVICE_NAME, "")
        FileWriter fileWriter = new FileWriter(serviceConfigFile)
        for (String service : services) {
            fileWriter.write(service)
            fileWriter.write("\n")
        }
        fileWriter.flush()
        fileWriter.close()

        ZipEntry serviceConfig = new ZipEntry(SERVICES_DIRECTORY + ISERVICE_NAME)
        byte[] newEntryContent = IOUtils.toByteArray(new FileInputStream(serviceConfigFile))
        crc32 = new CRC32()
        crc32.update(newEntryContent)
        serviceConfig.setCrc(crc32.getValue())
        serviceConfig.setMethod(ZipEntry.STORED)
        serviceConfig.setSize(newEntryContent.length)
        serviceConfig.setCompressedSize(newEntryContent.length)
        serviceConfig.setLastAccessTime(ZERO)
        serviceConfig.setLastModifiedTime(ZERO)
        serviceConfig.setCreationTime(ZERO)
        outputZip.putNextEntry(serviceConfig)
        outputZip.write(newEntryContent)
        outputZip.closeEntry()
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return super.wrapClassWriter(classWriter)
    }
}
