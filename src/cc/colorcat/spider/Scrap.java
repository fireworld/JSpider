package cc.colorcat.spider;

import cc.colorcat.spider.internal.Utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scrap {
    private final String tag;
    private final URI uri;
    private Map<String, String> data;

    public static List<Scrap> newScraps(String tag, List<String> uris) {
        int size = uris.size();
        List<Scrap> scraps = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            scraps.add(newScrap(tag, uris.get(i)));
        }
        return scraps;
    }

    public static Scrap newScrap(String tag, String uri) {
        return newScrap(tag, URI.create(uri));
    }

    public static Scrap newScrap(String tag, URI uri) {
        if (Utils.isEmpty(tag)) {
            throw new IllegalArgumentException("tag is empty");
        }
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        return new Scrap(tag, uri);
    }

    private Scrap(String tag, URI uri) {
        this(tag, uri, null);
    }

    private Scrap(String tag, URI uri, Map<String, String> data) {
        this.uri = uri;
        this.tag = tag;
        this.data = data;
    }

    public URI uri() {
        return this.uri;
    }

    public String tag() {
        return this.tag;
    }

    public Map<String, String> data() {
        return Utils.immutableMap(data);
    }

    public Scrap fill(Map<String, String> data) {
        if (data != null) {
            original().putAll(data);
        }
        return this;
    }

    public Scrap fill(String key, String value) {
        if (key != null && value != null) {
            original().put(key, value);
        }
        return this;
    }

    private Map<String, String> original() {
        if (data == null) {
            data = new HashMap<>();
        }
        return data;
    }

    public Scrap newScrap(String uri) {
        return newScrap(URI.create(uri));
    }

    public Scrap newScrap(URI uri) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        return new Scrap(tag, uri);
    }

    public Scrap newScrapWithJoin(String uri) {
        return newScrapWithJoin(URI.create(uri));
    }

    public Scrap newScrapWithJoin(URI uri) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        URI newUri = this.uri.resolve(uri);
        Map<String, String> newData = data != null ? new HashMap<>(data) : null;
        return new Scrap(tag, newUri, newData);
    }

    @Override
    public String toString() {
        return "Scrap{" +
                "tag='" + tag + '\'' +
                ", uri=" + uri +
                ", data=" + data +
                '}';
    }
}
