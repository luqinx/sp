package chao.app.remoteexample;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.lang.reflect.Method;
import java.util.Arrays;

import chao.android.tools.servicepool.Spa;
import chao.java.tools.servicepool.ClassTypeAdapter;
import chao.app.remoteapi.IExampleService;

/**
 * @author luqin
 * @since 2020-07-23
 */
public class LaunchActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Spa.init(this);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Class.class, new ClassTypeAdapter())
//                .registerTypeAdapter(Class[].class, new ClassArrayTypeAdapter())
                .create();

        try {
            Method m = IExampleService.class.getMethod("withII", int.class, int.class);

//            TypeToken<Class> typeToken = new TypeToken<>();

            JsonArray je = gson.toJsonTree(m.getParameterTypes()).getAsJsonArray();
            System.out.println(je);

            Class[] c = gson.fromJson(je, Class[].class);

            System.out.println(Arrays.toString(c));


            System.out.println(Class.forName("java.util.List"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
