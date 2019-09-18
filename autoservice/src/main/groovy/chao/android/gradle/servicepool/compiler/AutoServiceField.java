package chao.android.gradle.servicepool.compiler;

/**
 * @author luqin
 * @since  2019-07-15
 */
public class AutoServiceField {
    public String name;
    public String desc;
    public String signature;
    public Object value;
    public String asmFullName;
    public String annotationValue;
    public String annotationPath;
    public boolean isStatic;

    @Override
    public String toString() {
        return "AutoServiceField{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", signature='" + signature + '\'' +
                ", value=" + value +
                '}';
    }
}
