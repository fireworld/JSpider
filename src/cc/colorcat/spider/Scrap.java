package cc.colorcat.spider;

import java.net.URI;

public class Scrap {
    private final URI uri;
    private String tag;
    private Object extra;

    public Scrap(URI uri) {
        this(uri, "", null);
    }

    public Scrap(URI uri, String tag) {
        this(uri, tag, null);
    }

    public Scrap(URI uri, String tag, Object extra) {
        this.uri = uri;
        this.tag = tag;
        this.extra = extra;
    }

    public URI uri() {
        return this.uri;
    }

    public String tag() {
        return this.tag;
    }

    @SuppressWarnings("unchecked")
    public <T> T extra() {
        return (T) this.extra;
    }

    public Scrap newScrap(Object extra) {
        return newScrap("", extra);
    }

    public Scrap newScrap(String tag, Object extra) {
        return new Scrap(this.uri, tag, extra);
    }

    public Scrap newScrapWithJoin(String uri, String tag) {
        return newScrapWithJoin(uri, tag, null);
    }

    public Scrap newScrapWithJoin(String uri, String tag, Object extra) {
        return newScrapWithJoin(URI.create(uri), tag, extra);
    }

    public Scrap newScrapWithJoin(URI uri, String tag) {
        return newScrapWithJoin(uri, tag, null);
    }

    public Scrap newScrapWithJoin(URI uri, String tag, Object extra) {
        URI newUri = this.uri.resolve(uri);
        return new Scrap(newUri, tag, extra);
    }

    @Override
    public String toString() {
        return "Scrap{" +
                "uri=" + uri +
                ", tag='" + tag + '\'' +
                ", extra=" + extra +
                '}';
    }
}
