package chao.android.gradle.plugin.base.extension

import chao.android.gradle.plugin.maven.MavenExtension
import chao.android.gradle.plugin.normalization.JacocoExtension
import chao.android.gradle.plugin.Constant
import chao.android.gradle.plugin.version.ACVersionExtension
import org.gradle.api.NamedDomainObjectFactory

import javax.annotation.Nonnull
/**
 * @author qinchao
 * @since 2019/4/18
 */
class ExtensionFactory implements NamedDomainObjectFactory<DefaultExtension> {

    @Override
    DefaultExtension create(@Nonnull String name) {
        return getExtensionByName(name)
    }

    static DefaultExtension getExtensionByName(String name) {
        switch (name) {
            case Constant.extension.JACOCO:
                return new JacocoExtension(name)
            case Constant.extension.MAVEN:
                return new MavenExtension(name)
            case Constant.extension.AC_VERSION:
                return new ACVersionExtension(name)
            default:
                return new DefaultExtension(name)
        }
    }
}
