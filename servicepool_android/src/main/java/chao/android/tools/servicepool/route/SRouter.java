package chao.android.tools.servicepool.route;

/**
 * @author luqin
 * @since 2019-07-31
 */
public class SRouter {

    public static RouteBuilder build(String path) {
        return new RouteBuilder(path);
    }
}
