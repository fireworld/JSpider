package cc.colorcat.spider;

import cc.colorcat.spider.internal.Utils;
import com.sun.deploy.util.URLUtil;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public class Scrap {
    private final String tag;
    private final URI uri;
    private final int depth;
    private Map<String, String> data;

    static List<Scrap> newScraps(String tag, List<String> uris) {
        return newScraps(tag, uris, Collections.emptyMap());
    }

    static List<Scrap> newScraps(String tag, List<String> uris, Map<String, String> defData) {
        int size = uris.size();
        List<Scrap> scraps = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            scraps.add(newScrap(tag, uris.get(i), defData));
        }
        return scraps;
    }

    static Scrap newScrap(String tag, String uri) {
        return newScrap(tag, uri, Collections.emptyMap());
    }

    static Scrap newScrap(String tag, String uri, Map<String, String> defData) {
        return newScrap(tag, URI.create(uri), defData);
    }

    static Scrap newScrap(String tag, URI uri) {
        return newScrap(tag, uri, Collections.emptyMap());
    }

    static Scrap newScrap(String tag, URI uri, Map<String, String> defData) {
        if (Utils.isEmpty(tag)) {
            throw new IllegalArgumentException("tag is empty");
        }
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        if (defData == null) {
            throw new NullPointerException("defData == null");
        }
        return new Scrap(tag, uri, new HashMap<>(defData));
    }

    private Scrap(String tag, URI uri, Map<String, String> data) {
        this(tag, uri, 0, data);
    }

    private Scrap(String tag, URI uri, int depth, Map<String, String> data) {
        this.tag = tag;
        this.uri = uri;
        this.depth = depth;
        this.data = data;
    }

    public String tag() {
        return this.tag;
    }

    public URI uri() {
        return this.uri;
    }

    public String baseUrl() {
        try {
            return URLUtil.getBase(uri.toURL()).toString();
        } catch (MalformedURLException e) {
            return uri.toString();
        }
    }

    public Map<String, String> data() {
        return Utils.immutableMap(data);
    }

    public int depth() {
        return depth;
    }

    public Scrap fill(Map<String, String> data) {
        if (data != null) {
            this.data.putAll(data);
        }
        return this;
    }

    public Scrap fillIfAbsent(Map<String, String> data) {
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                if (!this.data.containsKey(key)) {
                    this.data.put(key, entry.getValue());
                }
            }
        }
        return this;
    }

    public Scrap fill(String key, String value) {
        if (key != null && value != null) {
            data.put(key, value);
        }
        return this;
    }

    public Scrap fillIfAbsent(String key, String value) {
        if (key != null && value != null && !data.containsKey(key)) {
            data.put(key, value);
        }
        return this;
    }

    public Scrap newScrapWithFill(String key, String value) {
        if (key == null || value == null) {
            throw new NullPointerException("key or value is null");
        }
        return new Scrap(tag, uri, depth + 1, new HashMap<>(data)).fill(key, value);
    }

    public Scrap newScrapWithFill(Map<String, String> data) {
        if (data == null) {
            throw new NullPointerException("data == null");
        }
        return new Scrap(tag, uri, depth + 1, new HashMap<>(this.data)).fill(data);
    }

    public Scrap newScrap(String uri) {
        return newScrap(URI.create(uri));
    }

    public Scrap newScrap(URI uri) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        return new Scrap(tag, uri, depth + 1, new HashMap<>(data));
    }

    public Scrap newScrapWithJoin(String uri) {
        return newScrapWithJoin(URI.create(uri));
    }

    public Scrap newScrapWithJoin(URI uri) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        URI newUri = this.uri.resolve(uri);
        return new Scrap(tag, newUri, depth + 1, new HashMap<>(data));
    }

    @Override
    public String toString() {
        return "Scrap{" +
                "tag='" + tag + '\'' +
                ", uri=" + uri +
                ", depth=" + depth +
                ", data=" + data +
                '}';
    }
}
