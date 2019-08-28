package chao.android.gradle.plugin.dependencies

import org.xml.sax.SAXException

import javax.xml.parsers.ParserConfigurationException
/**
 * @author qinchao
 * @since 2018/11/16
 */
@Deprecated
class ModuleXmlParser {

    static def parse(File file) {
        try {
            Map<String, Module> data = new HashMap<>()
            Map<String, List<Module>> groupIds = new HashMap<>()
            XmlParser xmlParser = new XmlParser()
            def modules = xmlParser.parse(file)
            modules.each { module ->
                Module m = new Module()
                m.name = module.attribute("name")

                m.useProject = module.attribute("select") == "project"
                m.projectName = module.project.text()
                m.remote = module.remote.text()

                def remoteSplit = m.remote.split(":")
                if (remoteSplit.length != 3) {
                    throw new IllegalStateException("配置xml格式不正确, remote格式错误 :" + m.remote)
                }
                m.setGroupId(remoteSplit[0])
                m.setArtifactId(remoteSplit[1])
                m.setVersion(remoteSplit[2])

                if (!m.name) {
                    throw new IllegalStateException("配置xml格式不正确, module必须指定name属性: " + m)
                }

                if (data.containsKey(m.name)){
                    throw new IllegalStateException("模块名称已存在: " + m.name)
                }
                data.put(m.name, m)

                List<Module> groupModules = groupIds.get(m.getGroupId())
                if (groupModules == null) {
                    groupModules = new ArrayList<>()
                    groupIds.put(m.getGroupId(), groupModules)
                }
                groupModules.add(m)
            }
            return [data, groupIds]

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace()
        }
    }
}
