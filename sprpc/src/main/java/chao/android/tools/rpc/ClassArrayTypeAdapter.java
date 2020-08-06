package chao.android.tools.rpc;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luqin
 * @since 2020-07-24
 */
public class ClassArrayTypeAdapter extends TypeAdapter<Class[]> {

    @Override
    public void write(JsonWriter out, Class[] value) throws IOException {
        out.beginArray();
        for (Class clazz : value) {
            out.value(clazz.getName());
        }
        out.endArray();
    }

    @Override
    public Class[] read(JsonReader in) throws IOException {
        List<Class> classList = new ArrayList<>();
        while (in.hasNext()) {
            String serialize = in.nextString();
            Class clazz = null;
            switch (serialize) {
                case "int":
                    clazz = int.class;
                    break;
                case "boolean":
                    clazz = boolean.class;
                    break;
                case "short":
                    clazz = short.class;
                    break;
                case "byte":
                    clazz = byte.class;
                    break;
                case "long":
                    clazz = long.class;
                    break;
                case "float":
                    clazz = float.class;
                    break;
                case "double":
                    clazz = double.class;
                    break;
                default:
                    try {
                        clazz = Class.forName(serialize);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            classList.add(clazz);
        }
        Class[] classes = new Class[classList.size()];
        classList.toArray(classes);
        return classes;
    }
}
