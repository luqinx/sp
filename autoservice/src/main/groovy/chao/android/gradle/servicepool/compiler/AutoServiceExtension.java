package chao.android.gradle.servicepool.compiler;

import java.util.ArrayList;
import java.util.List;

import groovy.lang.Closure;

/**
 * @author luqin
 * @date 2019-07-15
 */
public class AutoServiceExtension {

    private boolean debuggable;

    private List<String> excludes;

    public AutoServiceExtension() {
        excludes = new ArrayList<>();
        //排除掉android framework包， android support, androidx
        excludes.add("com.android.");
        excludes.add("android.");
        excludes.add("androidx.");
        excludes.add("com.google.");
        //排除掉一些常用的三方组件
        excludes.add("com.jakewharton.");
        excludes.add("com.squareup."); //排除掉retrofit等square库
        excludes.add("com.facebook.");

        excludes.add("io.reactivex.");
        excludes.add("org.apache.");
    }

    public List<String> excludes() {
        return excludes;
    }

    public void exclude(String name) {
        excludes.add(name);
    }

    public void debuggable(boolean debuggable) {
        this.debuggable = debuggable;
    }

    public boolean isDebuggable() {
        return debuggable;
    }
}
