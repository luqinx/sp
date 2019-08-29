package chao.android.gradle.servicepool.compiler;

import org.objectweb.asm.Type;

import java.util.List;

import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since  2019-07-13
 */
public class ServiceInfo implements Constant {

    private String pkgName;

    private String service;

    private String asmName;

    private String descriptor;

    private int scope;

    private int priority;

    private String tag;

    private boolean async;

    private List<Type> dependencies;

    public ServiceInfo(String name) {
        this.asmName = name;
        this.descriptor = "L" + name + ";";
        this.service = asmName.replaceAll("/", ".");
        int last = service.lastIndexOf('.');
        this.pkgName = last == -1 ? service : service.substring(0, last);
        this.scope = IService.Scope.global;
        this.priority = IService.Priority.NORMAL_PRIORITY;
        this.tag = "";
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAsmName() {
        return asmName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public void setAsmName(String asmName) {
        this.asmName = asmName;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public List<Type> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Type> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * 解析注解参数
     *
     * @param values 注解参数， 单数索引为key， 双数索引为value
     */
    public void parse(List<Object> values) {
        if (values == null || values.size() == 0 || values.size() % 2 != 0) {
            return;
        }
        for (int i = 0; i < values.size(); i += 2) {
            String key = String.valueOf(values.get(i));
            Object value = values.get(i + 1);
            if (METHOD_PRIORITY.equals(key)) {
                priority = (int) value;
            } else if (METHOD_SCOPE.equals(key)) {
                scope = (int) value;
            } else if (METHOD_TAG.equals(key)) {
                tag = String.valueOf(value);
            } else if (METHOD_ASYNC.equals(key)) {
                async = (boolean) value;
            } else if (METHOD_DEPENDENCIES.equals(key)) {
                dependencies = (List<Type>) value;
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ServiceInfo) {
            if (asmName == null && ((ServiceInfo) o).asmName == null) {
                return true;
            }
            if (asmName == null) {
                return false;
            }
            return asmName.equals(((ServiceInfo) o).asmName);
        }
        return false;
    }
}
