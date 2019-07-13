package chao.test.service_pools;

import com.example.testpluginlib.TestPluginService;

import static org.junit.Assert.assertEquals;


import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws InstantiationException, IllegalAccessException {
        long start = System.currentTimeMillis();
        TestPluginService pluginService = new TestPluginService();
        pluginService.print();
        long mid = System.currentTimeMillis();

        pluginService = TestPluginService.class.newInstance();
        pluginService.print();
        long end = System.currentTimeMillis();

        System.out.println(mid - start);
        System.out.println(end - mid);
    }
}