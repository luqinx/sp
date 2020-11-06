package chao.android.gradle.servicepool.hunter;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.ExtendedContentType;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.ide.common.internal.WaitableExecutor;
import com.google.common.io.Files;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import chao.android.gradle.servicepool.hunter.asm.BaseWeaver;
import chao.android.gradle.servicepool.hunter.asm.ClassLoaderHelper;

import static com.android.build.api.transform.QualifiedContent.DefaultContentType.CLASSES;
import static com.android.build.api.transform.QualifiedContent.DefaultContentType.RESOURCES;
import static com.android.build.api.transform.QualifiedContent.Scope.EXTERNAL_LIBRARIES;
import static com.android.build.api.transform.QualifiedContent.Scope.PROJECT;
import static com.android.build.api.transform.QualifiedContent.Scope.SUB_PROJECTS;

/**
 * Created by Quinn on 26/02/2017.
 * Transform to modify bytecode
 */
public class HunterTransform extends Transform {

    private final Logger logger;

    private static final Set<QualifiedContent.Scope> SCOPES = new HashSet<>();

    static {
        SCOPES.add(QualifiedContent.Scope.PROJECT);
        SCOPES.add(QualifiedContent.Scope.SUB_PROJECTS);
        SCOPES.add(QualifiedContent.Scope.EXTERNAL_LIBRARIES);
    }

    private Project project;
    protected BaseWeaver bytecodeWeaver;
    private WaitableExecutor waitableExecutor;
    private boolean emptyRun = false;

    public HunterTransform(Project project){
        this.project = project;
        this.logger = project.getLogger();
        this.waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool();
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        return SCOPES;
    }

    @Override
    public boolean isIncremental() {
        return true;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void transform(Context context,
                          Collection<TransformInput> inputs,
                          Collection<TransformInput> referencedInputs,
                          TransformOutputProvider outputProvider,
                          boolean isIncremental) throws IOException, TransformException, InterruptedException {
        RunVariant runVariant = getRunVariant();
        if("debug".equals(context.getVariantName())) {
            emptyRun = runVariant == RunVariant.RELEASE || runVariant == RunVariant.NEVER;
        } else if("release".equals(context.getVariantName())) {
            emptyRun = runVariant == RunVariant.DEBUG || runVariant == RunVariant.NEVER;
        }
        logger.warn(getName() + " isIncremental = " + isIncremental + ", runVariant = "
                + runVariant + ", emptyRun = " + emptyRun + ", inDuplcatedClassSafeMode = " + inDuplcatedClassSafeMode());
        long startTime = System.currentTimeMillis();
        if(!isIncremental) {
            outputProvider.deleteAll();
        }
        URLClassLoader urlClassLoader = ClassLoaderHelper.getClassLoader(inputs, referencedInputs, project);
        this.bytecodeWeaver.setClassLoader(urlClassLoader);
        boolean flagForCleanDexBuilderFolder = false;
        transformStart();
        for(TransformInput input : inputs) {
            for(JarInput jarInput : input.getJarInputs()) {
                Status status = jarInput.getStatus();
                File dest = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                if(isIncremental && !emptyRun) {
                    switch(status) {
                        case NOTCHANGED:
                            break;
                        case ADDED:
                        case CHANGED:
                            transformJar(jarInput, dest, status);
                            break;
                        case REMOVED:
                            if (dest.exists()) {
                                FileUtils.forceDelete(dest);
                            }
                            break;
                    }
                } else {
                    //Forgive me!, Some project will store 3rd-party aar for serveral copies in dexbuilder folder,,unknown issue.
                    if(inDuplcatedClassSafeMode() & !isIncremental && !flagForCleanDexBuilderFolder) {
                        cleanDexBuilderFolder(dest);
                        flagForCleanDexBuilderFolder = true;
                    }
                    transformJar(jarInput, dest, status);
                }
            }

            for(DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
                FileUtils.forceMkdir(dest);
                if(isIncremental && !emptyRun) {
                    String srcDirPath = directoryInput.getFile().getAbsolutePath();
                    String destDirPath = dest.getAbsolutePath();
                    Map<File, Status> fileStatusMap = directoryInput.getChangedFiles();
                    for (Map.Entry<File, Status> changedFile : fileStatusMap.entrySet()) {
                        Status status = changedFile.getValue();
                        File inputFile = changedFile.getKey();
                        String destFilePath = inputFile.getAbsolutePath().replace(srcDirPath, destDirPath);
                        File destFile = new File(destFilePath);
                        switch (status) {
                            case NOTCHANGED:
                                break;
                            case REMOVED:
                                if(destFile.exists()) {
                                    //noinspection ResultOfMethodCallIgnored
                                    destFile.delete();
                                }
                                break;
                            case ADDED:
                            case CHANGED:
                                try {
                                    FileUtils.touch(destFile);
                                } catch (IOException e) {
                                    //maybe mkdirs fail for some strange reason, try again.
                                    Files.createParentDirs(destFile);
                                }
                                transformSingleFile(inputFile, destFile, srcDirPath);
                                break;
                        }
                    }
                } else {
                    transformDir(directoryInput.getFile(), dest);
                }

            }

        }
        Set<QualifiedContent.ContentType> contentTypes = new HashSet<>();
        contentTypes.add(CLASSES);
        contentTypes.add(RESOURCES);

        Set<QualifiedContent.Scope> scopes = new HashSet<>();
//        scopes.add(EXTERNAL_LIBRARIES);
//        scopes.add(PROJECT);
        scopes.add(SUB_PROJECTS);

        File dest = outputProvider.getContentLocation(
                "autoservice",
                contentTypes,
                scopes,
                Format.JAR);


        waitableExecutor.waitForTasksWithQuickFail(true);
        transformFinished(dest);
        long costTime = System.currentTimeMillis() - startTime;
        urlClassLoader.close();
        logger.warn((getName() + " costed " + costTime + "ms"));
    }

    protected void transformFinished(File destJar) {

    }

    private void transformStart() {

    }

    private void transformSingleFile(final File inputFile, final File outputFile, final String srcBaseDir) {
        waitableExecutor.execute(() -> {
            bytecodeWeaver.weaveSingleClassToFile(inputFile, outputFile, srcBaseDir);
            return null;
        });
    }

    private void transformDir(final File inputDir, final File outputDir) throws IOException {
        if(emptyRun) {
//            FileUtils.copyDirectory(inputDir, outputDir);
            return;
        }
        final String inputDirPath = inputDir.getAbsolutePath();
        final String outputDirPath = outputDir.getAbsolutePath();
        if (inputDir.isDirectory()) {
            for (final File file : com.android.utils.FileUtils.getAllFiles(inputDir)) {
                waitableExecutor.execute(() -> {
                    String filePath = file.getAbsolutePath();
                    File outputFile = new File(filePath.replace(inputDirPath, outputDirPath));
                    bytecodeWeaver.weaveSingleClassToFile(file, outputFile, inputDirPath);
                    return null;
                });
            }
        }
    }

    private void transformJar(final JarInput srcInput, final File destJar, Status status) {

        waitableExecutor.execute(() -> {
            File srcJar = srcInput.getFile();
            if(emptyRun || weaverJarExcluded(srcInput.getName())) {
                FileUtils.copyFile(srcJar, destJar);
                return null;
            }
            bytecodeWeaver.weaveJar(srcJar, destJar);
            return null;
        });
    }

    private boolean weaverJarExcluded(String jarName) {
        return bytecodeWeaver.weaverJarExcluded(jarName);
    }


    private void cleanDexBuilderFolder(File dest) {
        waitableExecutor.execute(() -> {
            try {
                String dexBuilderDir = replaceLastPart(dest.getAbsolutePath(), getName(), "dexBuilder");
                //intermediates/transforms/dexBuilder/debug
                File file = new File(dexBuilderDir).getParentFile();
                project.getLogger().warn("clean dexBuilder folder = " + file.getAbsolutePath());
                if(file.exists() && file.isDirectory()) {
                    com.android.utils.FileUtils.deleteDirectoryContents(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private String replaceLastPart(String originString, String replacement, String toreplace) {
        int start = originString.lastIndexOf(replacement);
        StringBuilder builder = new StringBuilder();
        builder.append(originString.substring(0, start));
        builder.append(toreplace);
        builder.append(originString.substring(start + replacement.length()));
        return builder.toString();
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    protected RunVariant getRunVariant() {
        return RunVariant.ALWAYS;
    }

    protected boolean inDuplcatedClassSafeMode(){
        return false;
    }
}
