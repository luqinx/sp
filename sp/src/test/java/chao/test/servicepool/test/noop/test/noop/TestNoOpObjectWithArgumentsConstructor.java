package chao.test.servicepool.test.noop.test.noop;

/**
 * @author qinchao
 * @since 2019/5/5
 */
public class TestNoOpObjectWithArgumentsConstructor {

    private final Double db;
    private final Short s;
    private long l;
    private float f;

    public TestNoOpObjectWithArgumentsConstructor(int a, String b, boolean c, Object d, Long l, Float f, Short s, Double db) {
        this.f = f;
        this.l = l;
        this.s = s;
        this.db = db;
    }
}
