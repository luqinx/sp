package chao.android.gradle.servicepool;

/**
 * @author luqin  qinchao@mochongsoft.com
 * @project: zmjx-sp
 * @description:
 * @date 2019-07-09
 */
public class Logger {

    public static void log(String tag, Object... args) {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(tag).append(": ");
        for (Object arg: args) {
            sbuilder.append(arg).append(",");
        }
        System.out.println(sbuilder.toString());
    }
}
