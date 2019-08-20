package chao.android.gradle.plugin.dependencies

import chao.android.gradle.plugin.base.PluginException
import chao.android.gradle.plugin.util.StringUtils
import org.gradle.api.initialization.Settings

import java.util.function.Function
import java.util.stream.Collectors

/**
 * @author qinchao
 * @since 2019/5/29
 */
class ModuleHandler {

    private Settings settings

    private Map<String, ModuleBuilder> builders

    private static ModuleHandler sInstance

    static ModuleHandler instance() {
        if (sInstance == null) {
            synchronized (ModuleHandler.class) {
                if (sInstance == null) {
                    sInstance = new ModuleHandler()
                }
            }
        }
        return sInstance
    }

    private ModuleHandler() {
        builders = new HashMap<>()
    }

    void settings(Settings settings) {
        this.settings = settings
    }

    ModuleBuilder module(String moduleName, String remoteName, String projectName) {

        if (StringUtils.isEmpty(moduleName)) {
            throw new PluginException("invilid module: ${moduleName} -> ${remoteName} -> ${projectName}" )
        }
        ModuleBuilder moduleBuilder = new ModuleBuilder(this)
        moduleBuilder.name(moduleName).remote(remoteName).project(projectName)
        if (StringUtils.isEmpty(remoteName)) {
            project(moduleName, projectName)
        } else if (StringUtils.isEmpty(projectName)) {
            remote(moduleName, remoteName)
        }
        builders.put(moduleName, moduleBuilder)
        return moduleBuilder
    }

    ModuleBuilder project(String moduleName, String project) {
        if (StringUtils.isEmpty(project)) {
            throw new PluginException("invilid module: ${moduleName} -> ${project}" )
        }
        ModuleBuilder moduleBuilder = builders.get(moduleName)
        if (!moduleBuilder) {
            moduleBuilder = new ModuleBuilder(this)
            builders.put(moduleName, moduleBuilder)
        }
        moduleBuilder.name(moduleName).project(project)
        settings.include(project)
        return moduleBuilder
    }

    ModuleBuilder remote(String moduleName, String remoteName) {
        if (StringUtils.isEmpty(remoteName)) {
            throw new PluginException("invalid module: ${moduleName} -> ${remoteName}" )
        }
        ModuleBuilder moduleBuilder = builders.get(moduleName)
        if (!moduleBuilder) {
            moduleBuilder = new ModuleBuilder(this)
            builders.put(moduleName, moduleBuilder)
        }
        moduleBuilder.name(moduleName).remote(remoteName)
        return moduleBuilder
    }

    List<Module> getModules() {
        return new ArrayList<Module>(builders.values()).stream().map(new Function<ModuleBuilder, Module>() {
            @Override
            Module apply(ModuleBuilder moduleBuilder) {
                return moduleBuilder.build()
            }
        }).collect(Collectors.toList())

    }

}
