package chao.android.gradle.plugin.util;

import chao.android.gradle.plugin.Constant;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.gradle.api.Project;

public class Util {
    private static final int BUFFER_SIZE = 16384;

    public static boolean isAndroid(Project project) {
        return project.getPlugins().hasPlugin("com.android.library") || project.getPlugins().hasPlugin("com.android.application");
    }

    public static void copyResource(String name, File dest) throws IOException {
        FileOutputStream os = null;
        File parent = dest.getParentFile();
        if (parent != null && (!parent.exists())) {
            parent.mkdirs();
        }
        InputStream is = null;

        try {
            is = Util.class.getResourceAsStream("/" + name);
            os = new FileOutputStream(dest, false);

            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    public static File getBuildDir(Project project) {
        File buildDir = new File(project.getBuildDir(), Constant.OUTPUT_DIR);
        if (!buildDir.exists()) {
            if (!buildDir.mkdirs()) {
                logger.log("warning: make build directory failed!");
            }
        }
        return buildDir;
    }

    public static File getCheckStyleDir(Project project) {
        File checkStyleDir = new File(getBuildDir(project), Constant.OUTPUT_FILENAME_CHECKSTYLE);
        if (!checkStyleDir.exists()) {
            if (!checkStyleDir.mkdirs()) {
                logger.log("warning: make checkstyle directory failed!");
            }
        }
        return checkStyleDir;
    }
}