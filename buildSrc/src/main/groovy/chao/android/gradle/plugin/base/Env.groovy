package chao.android.gradle.plugin.base

import org.gradle.api.Project

/**
 * @author qinchao
 * @since 2019/4/22
 */
class Env {

    private static Project rootProject

    private static boolean mDebug

    private static Property rootProperties

    static Project getRootProject() {
        return rootProject
    }

    static void rootProject(Project project) {
        rootProject = project.rootProject
    }

    static boolean getDebug() {
        return mDebug
    }

    static void debug(boolean debug) {
        mDebug = debug
    }

    static Property getProperties() {
        return rootProperties
    }

    static void properties(Property property) {
        rootProperties = property
    }
}
