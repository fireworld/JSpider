package cc.colorcat.jspider.test;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.net.URI;

/**
 * Created by cxx on 17-11-15.
 * xx.ch@outlook.com
 */
public class UriAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (!URI.class.isAssignableFrom(rawType)) {
            return null;
        }
        return (TypeAdapter<T>) new UriAdapter();
    }

    private static class UriAdapter extends TypeAdapter<URI> {
        @Override
        public synchronized void write(JsonWriter out, URI value) throws IOException {
            out.value(value.toString());
        }

        @Override
        public synchronized URI read(JsonReader in) throws IOException {
            return URI.create(in.nextString());
        }
    }
}
