package chao.android.gradle.plugin;

public interface Constant {

    interface extension {

        String AC = "abkit";

        String AC_VERSION = "abVersion";

        String AC_DEPENDENCY = "AC_DEPENDENCY";

        String MAVEN = "maven";

        String JACOCO = "jacoco";

        String CHECKSTYLE = "checkstyle";

        String ASSEMBLE = "assemble";

        String LINT = "lint";
    }

    interface propertyKey {
        String PLUGIN_ENABLED = "abkit.enabled";

        String DEBUGGABLE = "abkit.debug";

        String DEBUG_TAG = "abkit.debug.tag";
    }

    interface buildType {
        String DEBUG = "debug";

        String RELEASE = "release";

        String ALL = "all";
    }

    String DEFAULT_GROUP_ID = "";

    String OUTPUT_DIR = "abkit";

    String OUTPUT_FILENAME_CHECKSTYLE = "checkstyle";
}