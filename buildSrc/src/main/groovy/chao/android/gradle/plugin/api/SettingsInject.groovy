package chao.android.gradle.plugin.api

import chao.android.gradle.plugin.base.Env
import chao.android.gradle.plugin.base.Property
import chao.android.gradle.plugin.dependencies.ModuleHandler
import org.gradle.TaskExecutionRequest
import org.gradle.api.Action
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.api.internal.SettingsInternal
import org.gradle.api.internal.initialization.ClassLoaderScope
import org.gradle.api.internal.initialization.ScriptHandlerFactory
import org.gradle.api.internal.plugins.DefaultObjectConfigurationAction
import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.configuration.ScriptPlugin
import org.gradle.configuration.ScriptPluginFactory
import org.gradle.groovy.scripts.ScriptSource
import org.gradle.groovy.scripts.TextResourceScriptSource
import org.gradle.internal.resource.BasicTextResourceLoader
import org.gradle.internal.resource.TextResource
import org.gradle.invocation.DefaultGradle

import java.lang.reflect.Array
import java.lang.reflect.Field

/**
 * @author qinchao
 * @since 2019/5/29
 */
class SettingsInject {

    private static final String DEFAULT_MODULES_SETTINGS_FILE = "modules.gradle"

    private static final String MODULES_SETTINGS_FILE_KEY = "modules.gradle.file"

    private static File modulesFile

    public static Property props

    static void inject(Settings settings, DefaultGradle gradle) {


        props = new Property()
        props.initStaticProperties(settings.getRootDir())

        String flavorValue = props.propertyResult("abkit.flavors").getValue()
        String buildTypeValue = props.propertyResult("abkit.buildTypes").getValue()

        def flavors = new HashSet<>()
        def buildTypes = new HashSet()

        flavors.addAll(flavorValue? flavorValue.split(":"): new String[0])
        buildTypes.addAll(buildTypeValue? buildTypeValue.split(":"): new String[0])

        buildTypes.add("debug")
        buildTypes.add("release")

        List<TaskExecutionRequest> requests = new ArrayList<>(gradle.startParameter.taskRequests)
        if (requests.size() == 0) {
            requests.add(null)
        }
        for (TaskExecutionRequest request:requests) {
            def args
            if (request == null || request.args.size() == 0) {
                args = []
                args.add(props.propertyResult('abkit.sync.flavor').value + props.propertyResult('abkit.sync.buildType').value)
                println("abkit: startParameter request is empty, add config request args:" + args)
            } else {
                args = request.args
            }
            println("abkit: startParameter request args: ${args}")
            for (String arg: args) {
                //查找flavors
                for (String flavor: flavors) {
                    flavor = flavor.toLowerCase()
                    if (arg != null && arg.toLowerCase().contains(flavor)) {
                        props.loadFlavorProperties(settings.getRootDir(), flavor)
                        break
                    }
                }
                //查找buildType
                for (String buildType: buildTypes) {
                    buildType = buildType.toLowerCase()
                    if (arg != null && arg.toLowerCase().contains(buildType)) {
                        props.loadBuildTypeProperties(settings.getRootDir(), buildType)
                        break
                    }
                }
            }
        }

        Env.properties(props)


        String modulesFileName = DEFAULT_MODULES_SETTINGS_FILE

        if (props.hasProperty(MODULES_SETTINGS_FILE_KEY)) {
            modulesFileName = props.propertyResult(MODULES_SETTINGS_FILE_KEY).value
            if (!modulesFileName.endsWith(".gradle")) {
                modulesFileName += ".gradle"
            }
        }
        println("abkit: modules config file:  " + modulesFileName)
        modulesFile = new File(settings.rootDir, modulesFileName)

        gradle.apply(new Action<ObjectConfigurationAction>() {
            @Override
            void execute(ObjectConfigurationAction objectConfigurationAction) {

                try {
                    Field scriptHandlerFactoryField = DefaultObjectConfigurationAction.class.getDeclaredField("scriptHandlerFactory")
                    Field configureFactoryField = DefaultObjectConfigurationAction.class.getDeclaredField("configurerFactory")

                    scriptHandlerFactoryField.setAccessible(true)
                    configureFactoryField.setAccessible(true)
                    ScriptHandlerFactory handlerFactory = (ScriptHandlerFactory) scriptHandlerFactoryField.get(objectConfigurationAction)
                    ScriptPluginFactory configurerFactory = configureFactoryField.get(objectConfigurationAction)

                    applySettingsScript(handlerFactory, configurerFactory, settings)
                } catch (NoSuchFieldException e) {
                    e.printStackTrace()
                } catch (IllegalAccessException e) {
                    e.printStackTrace()
                }

            }
        })


    }

    private static void applySettingsScript(ScriptHandlerFactory scriptHandlerFactory, ScriptPluginFactory configurerFactory, final SettingsInternal settings) {
        TextResource settingsResource = (new BasicTextResourceLoader()).loadFile("settings file", modulesFile)
        ScriptSource settingsScriptSource = new TextResourceScriptSource(settingsResource)
        ClassLoaderScope settingsClassLoaderScope = settings.getClassLoaderScope()
        ScriptHandler scriptHandler = scriptHandlerFactory.create(settingsScriptSource, settingsClassLoaderScope)
        ScriptPlugin configurer = configurerFactory.create(settingsScriptSource, scriptHandler, settingsClassLoaderScope, settings.getRootClassLoaderScope(), true)
        ModuleHandler handler = ModuleHandler.instance()
        handler.setSettings(settings)
        configurer.apply(handler)
    }

}
