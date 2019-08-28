package chao.android.gradle.plugin.version

import chao.android.gradle.plugin.base.extension.DefaultExtension

/**
 * @author qinchao
 * @since 2018/10/31
 */
class ACVersionExtension extends DefaultExtension {

    private int minSdkVersion

    private int compileSdkVersion

    private int targetSdkVersion
    
    private boolean force = true

    private String buildToolsVersion

    ACVersionExtension(String name) {
        super(name)
    }

    int getMinSdkVersion() {
        return minSdkVersion
    }

    int getCompileSdkVersion() {
        return compileSdkVersion
    }

    int getTargetSdkVersion() {
        return targetSdkVersion
    }

    String getBuildToolsVersion() {
        return buildToolsVersion
    }

    void minSdkVersion(int minSdkVersion) {
        this.minSdkVersion = minSdkVersion
    }

    void compileSdkVersion(int compileSdkVersion) {
        this.compileSdkVersion = compileSdkVersion
    }

    void targetSdkVersion(int targetSdkVersion) {
        this.targetSdkVersion = targetSdkVersion
    }

    void buildToolsVersion(String buildToolsVersion) {
        this.buildToolsVersion = buildToolsVersion
    }

    void force(boolean force) {
        this.force = force
    }

    boolean getForce() {
        return this.force
    }

    @Override
    String toString() {
        return "ACVersionExtenstion{" +
            "minSdkVersion='" + minSdkVersion + '\'' +
            ", compileSdkVersion='" + compileSdkVersion + '\'' +
            ", targetSdkVersion='" + targetSdkVersion + '\'' +
            ", buildToolsVersion='" + buildToolsVersion + '\'' +
            '}'
    }
}
