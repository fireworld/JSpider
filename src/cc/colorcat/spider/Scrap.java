package cc.colorcat.spider;

import com.sun.istack.internal.Nullable;

import java.net.URI;

public class Scrap<T> {
    private URI uri;
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

    public final Scrap<T> join(String url) {
        uri = uri.resolve(url);
        return this;
    }

    public final Scrap<T> join(URI uri) {
        this.uri = this.uri.resolve(uri);
        return this;
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
