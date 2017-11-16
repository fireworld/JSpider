package cc.colorcat.jspider.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by cxx on 17-11-15.
 * xx.ch@outlook.com
 */
public class JsonUtils {
    private static final Gson GSON;

    static {
        GSON = new GsonBuilder()
                .registerTypeAdapterFactory(new UriAdapterFactory())
                .create();
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static String toJson(List<String> names, List<String> values) {
        final int size = names.size();
        if (values.size() != size) {
            throw new IllegalArgumentException("names.size() != values.size()");
        }
        JsonObject obj = new JsonObject();
        for (int i = 0; i < size; ++i) {
            obj.addProperty(names.get(i), values.get(i));
        }
        return obj.toString();
    }

    public static <T> T fromJson(String json, Class<T> clz) {
        return GSON.fromJson(json, clz);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return GSON.fromJson(json, typeOfT);
    }

    private JsonUtils() {
        throw new AssertionError("no instance");
    }
}
