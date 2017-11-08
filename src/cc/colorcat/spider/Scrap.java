package cc.colorcat.spider;

import cc.colorcat.spider.internal.ParserProxy;
import com.sun.istack.internal.Nullable;

import java.net.URI;
import java.util.List;

public class Scrap<T> {
    private final URI uri;
    private final T data;
    private final ParserProxy<T> parser;
    private final Listener<? super T> listener;

    private Scrap(Builder<T> builder) {
        this.uri = builder.uri;
        this.data = builder.data;
        this.parser = builder.parser;
        this.listener = builder.listener;
    }

    public URI uri() {
        return this.uri;
    }

    public List<Scrap<? extends T>> tryParse(WebSnapshot snapshot) {
        return parser.parse(this, snapshot);
    }

    public boolean isTarget() {
        return data != null;
    }

    @Nullable
    public final T data() {
        return data;
    }

    void deliver() {
        if (listener != null) {
            if (isTarget()) {
                listener.onSuccess(data);
            } else {
                listener.onFailure(this);
            }
        }
    }

    public Builder<T> newBuilder() {
        return new Builder<>(this);
    }

    @Override
    public String toString() {
        return "Scrap{" +
                "uri=" + uri +
                ", data=" + data +
                ", parser=" + parser +
                ", listener=" + listener +
                '}';
    }


    public static class Builder<T> {
        private URI uri;
        private T data;
        private ParserProxy<T> parser = new ParserProxy<>();
        private Listener<? super T> listener;

        public Builder(String uri) {
            this(URI.create(uri));
        }

        public Builder(URI uri) {
            if (uri == null) {
                throw new NullPointerException("uri == null");
            }
            this.uri = uri;
        }

        private Builder(Scrap<T> scrap) {
            this.uri = scrap.uri;
            this.data = scrap.data;
            this.parser = scrap.parser;
            this.listener = scrap.listener;
        }

        public Builder<T> uri(String uri) {
            return uri(URI.create(uri));
        }

        public Builder<T> uri(URI uri) {
            if (uri == null) {
                throw new NullPointerException("uri == null");
            }
            this.uri = uri;
            return this;
        }

        public Builder<T> join(String uri) {
            return join(URI.create(uri));
        }

        public Builder<T> join(URI uri) {
            if (uri == null) {
                throw new NullPointerException("uri == null");
            }
            this.uri = this.uri.resolve(uri);
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> registerParser(Parser<T> parser) {
            this.parser.registerParser(parser);
            return this;
        }

        public Builder<T> listener(Listener<? super T> listener) {
            this.listener = listener;
            return this;
        }

        public Scrap<T> build() {
            if (uri == null) {
                throw new IllegalStateException("no uri");
            }
            return new Scrap<>(this);
        }
    }
}
