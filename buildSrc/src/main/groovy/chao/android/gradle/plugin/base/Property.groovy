package chao.android.gradle.plugin.base

import chao.android.gradle.plugin.util.logger
import org.gradle.api.Project
/**
 * 属性工具类
 *
 * Properties优先级:
 *      local.properties > properties > plugin.properties > gradle.properties
 *
 *
 * Properties分类
 *      local.properties  是本地属性集合,本地调试用的, 所以作为最高优先级
 *
 *      properties 全名称应该是 <plugin_name>.properties, 如{@link chao.android.gradle.plugin.maven.MavenPlugin}
 *                 对应的属性文件是maven.properties;
 *                 可以是project级的properties,也可以是rootProject级的properties,
 *                 如果同时存在,project级properties优先级更高
 *
 *      plugin.properties 所有ac-plugin插件的属性都可以定义在这里
 *
 *      gradle.properties gradle自带属性文件,所有gradle构建过程中用到的属性都可以定义在这里
 *
 *
 * 使用方式:
 *      Env.rootProperties.propertyResult("key").value
 *          or
 *      Property.load("xxx.properties").propertyResult("key").booleanValue(false)
 *          or
 *      Property.load("xxx.properties").propertyResult("key").matches(100) {
 *          // do something
 *      }
 *      ...
 *
 * 在gradle中通过 {@link chao.android.gradle.plugin.api.UtilExtension} 使用:
 *      def value = acUtil.property("key")
 *
 * @author qinchao
 * @since 2019/2/28
 */
class Property {

    private Properties properties

    private static Properties localProperties

    private static Properties gradleProperties

    private static Properties pluginProperties


    Property() {

    }

    def initStaticProperties(File rootDir) {
        try {
            localProperties = new Properties()
            File local = new File(rootDir,"local.properties")
            if (local.exists()) {
                localProperties.load(local.newInputStream())
                logger.logd("local properties ${localProperties}")
            } else {
                logger.logd("${local.path} not exists")
            }
        } catch (Throwable ignored) {
            // ignore
            ignored.printStackTrace()
        }
        try {
            File gradle = new File(rootDir, "gradle.properties")
            gradleProperties = new Properties()
            if (gradle.exists()) {
                gradleProperties.load(gradle.newInputStream())
                logger.logd("gradle properties ${gradleProperties}")
            } else {
                logger.logd("${gradle.path} not exists")
            }
        } catch (Throwable ignored) {
            ignored.printStackTrace()
        }
        try {
            File plugin = new File(rootDir,"plugin.properties")
            pluginProperties = new Properties()
            if (plugin.exists()) {
                pluginProperties.load(plugin.newInputStream())
                logger.logd("plugin properties ${pluginProperties}")
            } else {
                logger.logd("${plugin.path} not exists")
            }
        } catch (Throwable ignored) {
            ignored.printStackTrace()
        }
    }

    Property load(String name, Project project) {
        if (name == null || name == '') {
            return this
        }
        if (!name.endsWith(".properties")) {
            name = name + ".properties"
        }

        properties = new Properties()
        try {
            File pluginNameProperties = project.file(name)
            String logTag = "has no"
            if (pluginNameProperties.exists()) {
                logTag = "use project"
            } else {
                pluginNameProperties = project.rootProject.file(name)
                if (pluginNameProperties.exists()) {
                    logTag = "use root"
                }
            }
            if (pluginNameProperties.exists()) {
                properties.load(pluginNameProperties.newInputStream())
                logger.logd("${project.name} ${logTag} module ${name} ${properties}")
            }
        } catch (Throwable ignore){
            ignore.printStackTrace()
        }
        return this
    }

    PropertyResult propertyResult(String key) {
        String property = localProperties.getProperty(key)
        if (property != null) {
            return new PropertyResult(property)
        }
        if (properties != null) {
            property = properties.getProperty(key)
        }
        if (property != null) {
            return new PropertyResult(property)
        }

        if (property != null) {
            return new PropertyResult(property)
        }

        property = pluginProperties.getProperty(key)
        if (property != null) {
            return new PropertyResult(property)
        }



        return new PropertyResult(gradleProperties.getProperty(key))
    }

    class PropertyResult {
        private String mValue

        PropertyResult(String value) {
            this.mValue = value
        }

        boolean match(Object value) {
            if (value == null) {
                value = ""
            }
            String valueStr = String.valueOf(value)
            return valueStr.equalsIgnoreCase(mValue)
        }

        void matches(Object value, Closure closure) {
            closure.resolveStrategy = Closure.OWNER_ONLY
            if (this.mValue == String.valueOf(value)) {
                closure()
            } else if (String.valueOf(value).equalsIgnoreCase(this.mValue)) {
                closure()
            }
        }

        String getValue() {
            return mValue
        }

        int intValue(int defValue) {
            if (isEmpty()) {
                return defValue
            }
            try {
                return Integer.parseInt(mValue)
            } catch (Throwable ignore) {
                return defValue
            }
        }

        boolean booleanValue(boolean defValue) {
            if (isEmpty()) {
                return defValue
            }
            try {
                return Boolean.parseBoolean(mValue)
            } catch (Throwable ignore) {
                return defValue
            }
        }

        float floatValue(float defValue) {
            if (isEmpty()) {
                return defValue
            }
            try {
                return Float.parseFloat(mValue)
            } catch (Throwable ignore) {
                return defValue
            }
        }

        double doubleValue(double defValue) {
            if (isEmpty()) {
                return defValue
            }
            try {
                return Double.parseDouble(mValue)
            } catch (Throwable ignore) {
                return defValue
            }
        }

        long longValue(long defValue) {
            if (isEmpty()) {
                return defValue
            }
            try {
                return Long.parseLong(mValue)
            } catch (Throwable e) {
                return defValue
            }
        }

        boolean isEmpty() {
            return mValue == null
        }

    }

    boolean hasProperty(String property) {
        return propertyResult(property).value != null
    }
}
