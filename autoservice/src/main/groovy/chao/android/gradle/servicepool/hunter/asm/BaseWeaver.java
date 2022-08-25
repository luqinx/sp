package chao.android.gradle.servicepool.hunter.asm;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.FileTime;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import chao.android.gradle.servicepool.Logger;
import chao.android.gradle.servicepool.compiler.Constant;

/**
 * Created by quinn on 07/09/2018
 */
public abstract class BaseWeaver implements IWeaver{

    public static final FileTime ZERO = FileTime.fromMillis(0);

    public static final String FILE_SEP = File.separator;

    protected ClassLoader classLoader;

    public BaseWeaver() {
    }

    public final void weaveJar(File inputJar, File outputJar) throws IOException {

        InputStream originalFile = null;
        ZipFile inputZip = new ZipFile(inputJar);
        ZipOutputStream outputZip = new ZipOutputStream(new BufferedOutputStream(
                java.nio.file.Files.newOutputStream(outputJar.toPath())));
        Enumeration<? extends ZipEntry> inEntries = inputZip.entries();
        try {
            weaveJarStarted(outputZip.hashCode());

            while (inEntries.hasMoreElements()) {
                ZipEntry entry = inEntries.nextElement();
                if (entry.getName().endsWith("META-INF/services/chao.java.tools.servicepool.IService")) {
                    continue;
                }
                //不重复生成代码
                if (entry.getName() != null && entry.getName().startsWith(Constant.GENERATE_SERVICE_PACKAGE_NAME)) {
                    continue;
                }
                originalFile =
                        new BufferedInputStream(inputZip.getInputStream(entry));
                ZipEntry outEntry = new ZipEntry(entry.getName());
                byte[] newEntryContent;
                // seperator of entry name is always '/', even in windows
                if (!isWeavableClass(outEntry.getName().replace("/", "."))) {
                    newEntryContent = IOUtils.toByteArray(originalFile);
                } else {
                    newEntryContent = weaveSingleClassToByteArray(outputZip.hashCode(), originalFile);
                }
                CRC32 crc32 = new CRC32();
                crc32.update(newEntryContent);
                outEntry.setCrc(crc32.getValue());
                outEntry.setMethod(ZipEntry.STORED);
                outEntry.setSize(newEntryContent.length);
                outEntry.setCompressedSize(newEntryContent.length);
                outEntry.setLastAccessTime(ZERO);
                outEntry.setLastModifiedTime(ZERO);
                outEntry.setCreationTime(ZERO);
                outputZip.putNextEntry(outEntry);
                outputZip.write(newEntryContent);
                outputZip.closeEntry();
            }
            weaveJarFinished(outputZip.hashCode(), inputZip, outputZip);
        } finally {
            if (originalFile != null) {
                originalFile.close();
            }
            outputZip.flush();
            outputZip.close();
        }
    }

    /**
     * 起始
     * @param jarId jar识别id
     */
    protected void weaveJarStarted(int jarId) {

    }

    /**
     * 收尾
     * @param jarId  jar识别id
     * @param inputZip
     * @param outputZip  输出zip
     */
    protected void weaveJarFinished(int jarId, ZipFile inputZip, ZipOutputStream outputZip) {

    }

    public final void weaveSingleClassToFile(File inputFile, File outputFile, String inputBaseDir) throws IOException {
        FileOutputStream fos = null;
        InputStream inputStream = null;
        try {
            if (!inputBaseDir.endsWith(FILE_SEP)) inputBaseDir = inputBaseDir + FILE_SEP;
            if (isWeavableClass(inputFile.getAbsolutePath().replace(inputBaseDir, "").replace(FILE_SEP, "."))) {
                FileUtils.touch(outputFile);
                inputStream = new FileInputStream(inputFile);
                byte[] bytes = weaveSingleClassToByteArray(NO_JAR_ID, inputStream);
                fos = new FileOutputStream(outputFile);
                fos.write(bytes);
            } else {
                if (inputFile.isFile()) {
                    FileUtils.touch(outputFile);
                    FileUtils.copyFile(inputFile, outputFile);
                }
            }
        } finally {
            if (fos != null) {
                fos.flush();
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public final void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public byte[] weaveSingleClassToByteArray(int jarId, InputStream inputStream) throws IOException {
        ClassReader classReader = new ClassReader(inputStream);
        ClassWriter classWriter = new ExtendClassWriter(classLoader, ClassWriter.COMPUTE_MAXS);
        ClassVisitor classWriterWrapper = wrapClassWriter(classWriter);
        classReader.accept(classWriterWrapper, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    public void setExtension(Object extension) {

    }

    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return classWriter;
    }

    @Override
    public boolean isWeavableClass(String fullQualifiedClassName){
        return fullQualifiedClassName.endsWith(".class") && !fullQualifiedClassName.contains("R$") && !fullQualifiedClassName.contains("R.class") && !fullQualifiedClassName.contains("BuildConfig.class");
    }

    public boolean weaverJarExcluded(String jarName) {
        return false;
    }
}
