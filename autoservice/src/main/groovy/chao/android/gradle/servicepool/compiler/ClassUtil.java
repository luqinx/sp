package chao.android.gradle.servicepool.compiler;

/**
 * @author luqin
 * @since  2019-07-17
 */
public class ClassUtil {

    public static String class2Desc(Class<?> clazz) {
        return "L" + clazz.getName().replaceAll("\\.", "/") + ";";
    }
}
