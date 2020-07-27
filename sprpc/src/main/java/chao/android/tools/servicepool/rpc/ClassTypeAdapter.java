package chao.android.tools.servicepool.rpc;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * @author luqin
 * @since 2020-07-24
 */
public class ClassTypeAdapter extends TypeAdapter<Class> {
    @Override
    public void write(JsonWriter out, Class value) throws IOException {
        out.value(value.getName());
    }

    @Override
    public Class read(JsonReader in) throws IOException {
        Class clazz = Object.class;
        if (in.hasNext()) {
            String nextName = in.nextString();
            switch (nextName) {
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
                        clazz = Class.forName(nextName);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        return clazz;
    }
}
