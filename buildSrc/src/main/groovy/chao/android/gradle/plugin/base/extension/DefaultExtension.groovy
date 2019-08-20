package chao.android.gradle.plugin.base.extension;

/**
 * @author qinchao
 * @since 2019/4/17
 */
class DefaultExtension {

    private String name

    private boolean enabled = false

    private boolean mDebug = false

    private String mDebugTag

    DefaultExtension(String name) {
        this.name = name
    }

    boolean isDebug() {
        return mDebug
    }

    void debug(boolean debug) {
        mDebug = debug
    }

    String getDebugTag() {
        return mDebugTag
    }

    void debugTag(String tag) {
        mDebugTag = tag
    }

    boolean isEnabled() {
        return enabled
    }

    void enabled(boolean enabled) {
        this.enabled = enabled
    }

    String getName() {
        return name
    }

    void name(String name) {
        this.name = name
    }

}
