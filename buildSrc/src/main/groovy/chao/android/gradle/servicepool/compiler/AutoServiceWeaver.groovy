package chao.android.gradle.servicepool.compiler

import chao.android.gradle.servicepool.Logger
import chao.android.gradle.servicepool.hunter.asm.BaseWeaver
import chao.android.gradle.servicepool.hunter.asm.ExtendClassWriter
import chao.java.tools.servicepool.IService
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode

import java.nio.file.Files
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
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

    private static final String MANIFEST_MF = "META-INF/MANIFEST.MF"

    private static final String ISERVICE_NAME = IService.class.name

    private Map<Integer, List<String>> serviceConfigMap = new HashMap<>()

    private List<String> services = new ArrayList<>()

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

                    services.add(classNode.name.replaceAll("/", "."))
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
        classReader.accept(visitor, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

    @Override
    boolean isWeavableClass(String fullQualifiedClassName) {
        return super.isWeavableClass(fullQualifiedClassName)
    }

    @Override
    protected void weaveJarFinished(int jarId, ZipFile inputZip, ZipOutputStream outputZip) {
        super.weaveJarFinished(jarId, inputZip, outputZip)
        List<String> services = serviceConfigMap.get(jarId)
        if (services == null) {
            return
        }
        if (inputZip.getEntry(SERVICES_DIRECTORY) == null) {
            writeZipEntry(SERVICES_DIRECTORY, "", outputZip)
        }

        StringBuilder buffer = new StringBuilder()
        services.each { service->
            buffer.append(service).append("\n")
        }
//        ZipEntry entry = inputZip.getEntry(SERVICES_DIRECTORY + ISERVICE_NAME)
        writeZipEntry(SERVICES_DIRECTORY + ISERVICE_NAME, buffer.toString(), outputZip)

        if (inputZip.getEntry(MANIFEST_MF) == null) {
            writeZipEntry(MANIFEST_MF, "Manifest-Version: 1.0", outputZip)
        }

    }

    private static void writeZipEntry(String entryName, String content, ZipOutputStream outputZip) {
        ZipEntry zipEntry = new ZipEntry(entryName)
        byte[] newEntryContent = content.getBytes()
        CRC32 crc32 = new CRC32()
        crc32.update(newEntryContent)
        zipEntry.setCrc(crc32.getValue())
        zipEntry.setMethod(ZipEntry.STORED)
        zipEntry.setSize(newEntryContent.length)
        zipEntry.setCompressedSize(newEntryContent.length)
        zipEntry.setLastAccessTime(ZERO)
        zipEntry.setLastModifiedTime(ZERO)
        zipEntry.setCreationTime(ZERO)
        outputZip.putNextEntry(zipEntry)
        outputZip.write(newEntryContent)
        outputZip.closeEntry()
    }

    void transformFinished(File destJar) {

        ZipOutputStream outputZip = new ZipOutputStream(new BufferedOutputStream(
                Files.newOutputStream(destJar.toPath())))


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

        System.out.println("--------> " + destJar.absolutePath)

        ZipEntry outEntry = new ZipEntry(SERVICES_DIRECTORY + ISERVICE_NAME)

        byte[] newEntryContent = IOUtils.toByteArray(new FileInputStream(serviceConfigFile))

        crc32 = new CRC32()
        crc32.update(newEntryContent)
        outEntry.setCrc(crc32.getValue())
        outEntry.setMethod(ZipEntry.STORED)
        outEntry.setSize(newEntryContent.length)
        outEntry.setCompressedSize(newEntryContent.length)
        outEntry.setLastAccessTime(ZERO)
        outEntry.setLastModifiedTime(ZERO)
        outEntry.setCreationTime(ZERO)
        outputZip.putNextEntry(outEntry)
        outputZip.write(newEntryContent)
        outputZip.closeEntry()

        outputZip.flush()
        outputZip.close()

    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return super.wrapClassWriter(classWriter)
    }
}
