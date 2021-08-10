package chao.test.servicepool.test.noop.test;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IInitService;
import java.util.Arrays;
import org.junit.Test;

/**

 * @author qinchao
 * @since 2019/5/1
 */
public class SampleTest {

    @Test
    public void test() {
        System.out.println(Arrays.toString(IInitService.class.getInterfaces()));

        System.out.println(Arrays.toString(IService.class.getInterfaces()));

        System.out.println(Arrays.toString(IInitService.class.getGenericInterfaces()));

        System.out.println(Arrays.toString(IService.class.getGenericInterfaces()));

    }
}
