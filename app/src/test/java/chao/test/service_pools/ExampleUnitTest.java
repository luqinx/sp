package chao.test.service_pools;

import com.example.testpluginlib.TestPluginService;

import static org.junit.Assert.assertEquals;


import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import chao.app.pool.IA;
import chao.app.pool.IB;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.annotation.Service;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private ArrayList<String> strings = new ArrayList<>();
    @Test
    public void addition_isCorrect() {

        test((Class<ArrayList<String>>) strings.getClass());
    }

    private void test(Class<ArrayList<String>> clazz) {
        System.out.println(clazz.getComponentType());
        System.out.println(Arrays.toString(clazz.getClasses()));
        System.out.println(clazz.getName());
        System.out.println(clazz.getGenericSuperclass());
    }
}