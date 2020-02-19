package chao.android.gradle.plugin.maven

import chao.android.gradle.plugin.base.BasePlugin
import chao.android.gradle.plugin.base.PluginException
import chao.android.gradle.plugin.Constant
import chao.android.gradle.plugin.util.StringUtils
import chao.android.gradle.plugin.util.Util
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class MavenPlugin extends BasePlugin<MavenExtension> {

    String DEFAULT_ACCOUNT = ""

    String DEFAULT_PASSWORD = ""

    String DEFAULT_RELEASE_NEXUS_URL = "http://47.99.188.223:8081/repository/maven-releases/"

    String DEFAULT_SNAPSHOT_NEXUS_URL="http://47.99.188.223:8081/repository/maven-snapshots/"

    final String USER_HOME = System.getProperty("user.home")

    MavenPlugin(Project project) {
        super(project)
    }

    void apply(Project project) {
        mavenUpload(project)
    }

    @Override
    String bindExtensionName() {
        return Constant.extension.MAVEN
    }

    @Override
    boolean enabledAsDefault() {
        return false
    }

    @Override
    boolean isEnabled() {
        return extension != null
    }

    def mavenUpload(Project project) {

        project.apply plugin: 'maven'

//        Properties props = new Properties()
//        File file = new File(project.rootDir, "maven.properties")
//        if (file.exists()) {
//            props.load(new FileInputStream(file))
//        }
        def account = getPluginProperty("nexusUserName").value
        def password = getPluginProperty("nexusPassword").value
        def publishRelease = getPluginProperty("publishRelease").booleanValue(false)

        if (!account || !password) {
            account = DEFAULT_ACCOUNT
            password = DEFAULT_PASSWORD
        }

        def releaseNexusUrl = getPluginProperty("nexusUrl").value
        if (!releaseNexusUrl) {
            releaseNexusUrl = DEFAULT_RELEASE_NEXUS_URL
        }

        def snapshotNexusUrl = getPluginProperty("nexusSnapshotUrl").value
        if (!snapshotNexusUrl) {
            snapshotNexusUrl = DEFAULT_SNAPSHOT_NEXUS_URL
        }

        def ADMIN_ACCOUNT = getPluginProperty("adminUserName").value
        def ADMIN_PASSWORD = getPluginProperty("adminPassword").value

        if (Util.isAndroid(project)) {
            createAndroidTasks(project)
        } else {
            createJavaTasks(project)
        }

        project.artifacts {
            if (Util.isAndroid(project)) {
                archives project.tasks.findByName('androidSourceJar')
            } else {
                archives project.tasks.findByName('javaSourcesJar')
            }
        }

        project.uploadArchives {

            def PUBLISH_ACCOUNT = publishRelease ? ADMIN_ACCOUNT : account
            def PUBLISH_PASSWORD = publishRelease ? ADMIN_PASSWORD : password
            def PUBLISH_VERSION = publishRelease ? extension.versionName : extension.versionName + "-SNAPSHOT"
            def PUBLISH_PACKAGING = Util.isAndroid(project) ? "aar" : "jar"
            def LOCAL_URL = "file://$USER_HOME/.m2/repository/"

            def PUBLISH_URL = extension.publish2Local ? LOCAL_URL: publishRelease ? releaseNexusUrl : snapshotNexusUrl

            def wholeName = extension.groupId + ":" + extension.artifactId + ":" + PUBLISH_VERSION

            repositories {
                mavenDeployer {

                    if (StringUtils.isEmpty(extension.groupId)
                            || StringUtils.isEmpty(extension.artifactId)
                            || StringUtils.isEmpty(extension.versionName)) {
                        throw new PluginException("发布信息不完整: " + wholeName + " on " + project)
                    }

                    repository(url: PUBLISH_URL) {
                        authentication(userName: PUBLISH_ACCOUNT, password: PUBLISH_PASSWORD)
                    }
                    pom.project {
                        groupId extension.groupId
                        artifactId extension.artifactId
                        version PUBLISH_VERSION
                        packaging PUBLISH_PACKAGING
                    }
                }
            }
            doFirst {
                println("准备打包上传..." )
                println("版本名称:" + wholeName)
                println("发布类型:" + (publishRelease ? "release" : "snapshot"))
                println("本地仓库:" + extension.publish2Local)
                println("仓库地址:" + PUBLISH_URL)

                //发布release版本必须使用admin账号
                if (publishRelease && (!ADMIN_ACCOUNT || !ADMIN_PASSWORD)) {
                    throw new PluginException("发布release版本需要admin账号，请联系管理员填写admin账号和密码。")
                }

                //检查依赖项，是否有project(':xxxx')依赖， 工程直接依赖会导致上传到仓库的pom引用问题
                project.configurations.findAll {
                    it.dependencies.findAll {
                        if (DefaultProjectDependency.isAssignableFrom(it.getClass())) {
                            throw new PluginException("maven上传时不支持使用project依赖 " + it)
                        }
                    }
                }
            }

            doLast {
                println("上传成功")
            }
        }
    }

    static void createJavaTasks(Project subproject) {

        subproject.tasks.create("javaSourcesJar", Jar, new Action<Jar>() {
            @Override
            void execute(Jar jar) {
                jar.classifier = 'sources'
                jar.from subproject.sourceSets.main.java.srcDirs
            }
        } )
    }

    static void createAndroidTasks(subproject) {

        subproject.tasks.create("androidSourceJar", Jar, new Action<Jar>() {
            @Override
            void execute(Jar jar) {
                jar.classifier = 'sources'
                jar.from subproject.android.sourceSets.main.java.srcDirs
            }
        })

        subproject.tasks.create("androidJavadocs", Javadoc, new Action<Javadoc>() {
            @Override
            void execute(Javadoc javadoc) {
                javadoc.source = subproject.android.sourceSets.main.java.srcDirs
                javadoc.classpath += subproject.files(subproject.android.getBootClasspath().join(File.pathSeparator))
            }
        })
    }
}