package chao.java.tools.servicepool.debug;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luqin
 * @since 2019-08-26
 */
public class Debug {

    private static List<String> errors = new ArrayList<>();

    private static List<Throwable> throwables = new ArrayList<>();

    public static void addError(String err) {
        if (err != null && err.length() != 0) {
            errors.add(err);
        }
    }

    public static void addThrowable(Throwable e) {
        if (e != null) {
            throwables.add(e);
        }
    }

    public static List<Throwable> throwables() {
        return throwables;
    }

    public static List<String> errors() {
        return errors;
    }
}
