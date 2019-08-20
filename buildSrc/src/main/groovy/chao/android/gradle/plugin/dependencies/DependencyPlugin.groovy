package chao.android.gradle.plugin.dependencies

import chao.android.gradle.plugin.base.BasePlugin
import chao.android.gradle.plugin.Constant
import org.gradle.api.Project
/**
 *
 * 检索version.properties中项目
 *
 * @author qinchao
 * @since 2018/11/14
 */
class DependencyPlugin extends BasePlugin {


    private static final List<String> DEPENDENCY_CONFIGURATION_LIST = new ArrayList<>()


    private static final String MODULES_GRADLE_FILE_NAME = "modules.gradle"


    private ModuleHandler handler

    private List<Module> modules


    DependencyPlugin(Project project) {
        super(project)
        handler = ModuleHandler.instance()
    }

    static {
        DEPENDENCY_CONFIGURATION_LIST.add("annotationProcessor")
        DEPENDENCY_CONFIGURATION_LIST.add("api")
        DEPENDENCY_CONFIGURATION_LIST.add("compile")
        DEPENDENCY_CONFIGURATION_LIST.add("implementation")
        DEPENDENCY_CONFIGURATION_LIST.add("runtime")
        DEPENDENCY_CONFIGURATION_LIST.add("provided")
        DEPENDENCY_CONFIGURATION_LIST.add("privateApi")
    }

    @Override
    void applyRoot() {

        File moduleGradle = new File(project.getRootDir(), MODULES_GRADLE_FILE_NAME)
        if (moduleGradle.exists()) {
            modules = handler.getModules()
        } else {
            modules = new ArrayList<>()
        }
        println(modules)

        getProject().rootProject.subprojects { subproject ->


            subproject.beforeEvaluate {

                //使用Module名作为依赖入口名， privateApi xxxx
                for (Module module : modules) {
                    def orgName = subproject.extensions.findByName(module.name)
                    if (orgName) {
                        println("??????? ====> " + orgName)
                        continue
                    }
                    if (module.useProject) {
                        subproject.extensions.add(module.name, project.project(module.project))
                    } else {
                        println("${module.name} - ${module.remote}")
                        subproject.extensions.add(module.name, module.remote)
                    }
                }
            }


            subproject.afterEvaluate {

                //依赖替换方案
                //详见: https://docs.gradle.org/current/dsl/org.gradle.api.artifacts.ResolutionStrategy.html
                subproject.configurations.all { configuration ->
                    configuration.resolutionStrategy.dependencySubstitution { strategy ->
                            modules.each { module ->
//                                substitute module(value.groupId + ":" + value.artifactId) with module(value.remote)
                                String from = module.groupId + ":" + module.artifactId
                                if (module.useProject) {
//                                    def to = rootProject.rootProject(value.projectName)
                                    strategy.substitute(strategy.module(from)).with(strategy.project(module.project))
                                } else {
                                    def to = module.remote
                                    strategy.substitute(strategy.module(from)).with(strategy.module(to))
                                }
                            }
                    }
                }
            }
        }

    }


    @Override
    String bindExtensionName() {
        return Constant.extension.AC_DEPENDENCY
    }

    @Override
    boolean enabledAsDefault() {
        return true
    }
}
