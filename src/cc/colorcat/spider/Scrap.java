package cc.colorcat.spider;

import com.sun.istack.internal.Nullable;

import java.net.URI;

public class Scrap<T> {
    private final URI uri;
    private T data;

    public Scrap(URI uri, T data) {
        this.uri = uri;
        this.data = data;
    }

    public Scrap(URI uri) {
        this.uri = uri;
    }

    public final URI uri() {
        return this.uri;
    }

    @Nullable
    public final T data() {
        return data;
    }

    public boolean isTarget() {
        return data != null;
    }

    @Override
    public String toString() {
        return "Scrap{" +
                "uri=" + uri +
                ", data=" + data +
                '}';
    }
}
