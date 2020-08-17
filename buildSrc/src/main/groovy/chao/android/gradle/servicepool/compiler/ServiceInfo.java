package chao.android.gradle.servicepool.compiler;

import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

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

    private boolean disableIntercept;

    private List<String> paths;

    private boolean async;

    private List<Type> dependencies;

    private boolean lazy = true;

    /**
     *  是否是InitService
     */
    private boolean isInit;

    public ServiceInfo(String name) {
        this.asmName = name;
        this.descriptor = "L" + name + ";";
        this.service = asmName.replaceAll("/", ".");
        int last = service.lastIndexOf('.');
        this.pkgName = last == -1 ? service : service.substring(0, last);
        this.scope = 0xf0000000 | 3; //默认scope
        this.priority = 3;
        this.disableIntercept = false;
        this.paths = new ArrayList<>();
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

    public int getPriority() {
        return priority;
    }

    public boolean isDisableIntercept() {
        return disableIntercept;
    }

    public String getAsmName() {
        return asmName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public List<String> getPaths() {
        return paths;
    }

    public boolean isAsync() {
        return async;
    }


    public List<Type> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Type> dependencies) {
        this.dependencies = dependencies;
    }

    public boolean isLazy() {
        return lazy;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
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
            } else if (METHOD_DISABLE_INTERCEPT.equals(key)) {
                disableIntercept = (boolean) value;
            } else if (METHOD_PATH.equals(key)){
                paths.add(String.valueOf(value));
            } else if (METHOD_ASYNC.equals(key)) {
                async = (boolean) value;
            } else if (METHOD_DEPENDENCIES.equals(key)) {
                dependencies = (List<Type>) value;
            } else if (METHOD_LAZY.equals(key)) {
                lazy = (boolean) value;
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

    @Override
    public String toString() {
        return "ServiceInfo{" +
                "descriptor='" + descriptor + '\'' +
                ", scope=" + scope +
                ", priority=" + priority +
                ", paths=" + paths +
                '}';
    }
}
