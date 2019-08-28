package chao.android.gradle.plugin.maven

import chao.android.gradle.plugin.Constant
import chao.android.gradle.plugin.base.extension.DefaultExtension
/**
 * @author qinchao
 * @since 2018/11/12
 */
class MavenExtension extends DefaultExtension {

    String versionName

    String groupId = Constant.DEFAULT_GROUP_ID

    String artifactId

    MavenExtension(String name) {
        super(name)
    }

    String getVersionName() {
        return versionName
    }

    void versionName(String versionName) {
        this.versionName = versionName
    }

    String getGroupId() {
        return groupId
    }

    void groupId(String groupId) {
        this.groupId = groupId
    }

    String getArtifactId() {
        return artifactId
    }

    void artifactId(String artifactId) {
        this.artifactId = artifactId
    }
}
