package cc.colorcat.spider;

import cc.colorcat.spider.internal.Utils;
import com.sun.deploy.util.URLUtil;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;

/**
 * Created by cxx on 2017/11/13.
 * xx.ch@outlook.com
 */
public class Seed {
    final String tag;
    final URI uri;
    final int depth;
    final Map<String, String> data;

    public static List<Seed> newSeeds(String tag, List<String> uris) {
        return newSeeds(tag, uris, 0, Collections.emptyMap());
    }

    public static List<Seed> newSeeds(String tag, List<String> uris, Map<String, String> defData) {
        return newSeeds(tag, uris, 0, defData);
    }

    public static List<Seed> newSeeds(String tag, List<String> uris, int depth) {
        return newSeeds(tag, uris, depth, Collections.emptyMap());
    }

    public static List<Seed> newSeeds(String tag, List<String> uris, int depth, Map<String, String> defData) {
        int size = uris.size();
        List<Seed> seeds = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            seeds.add(newSeed(tag, uris.get(i), depth, defData));
        }
        return seeds;
    }

    public static Seed newSeed(String tag, String uri) {
        return newSeed(tag, uri, 0, Collections.emptyMap());
    }

    public static Seed newSeed(String tag, String uri, int depth) {
        return newSeed(tag, uri, depth, Collections.emptyMap());
    }

    public static Seed newSeed(String tag, String uri, Map<String, String> defData) {
        return newSeed(tag, uri, 0, defData);
    }

    public static Seed newSeed(String tag, String uri, int depth, Map<String, String> defData) {
        return newSeed(tag, URI.create(uri), depth, defData);
    }

    public static Seed newSeed(String tag, URI uri, int depth, Map<String, String> defData) {
        if (Utils.isEmpty(tag)) {
            throw new IllegalArgumentException("tag is empty");
        }
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        if (depth < 0) {
            throw new IllegalArgumentException("depth < 0");
        }
        if (defData == null) {
            throw new NullPointerException("defData == null");
        }
        return new Seed(tag, uri, depth, new HashMap<>(defData));
    }

    Seed(String tag, URI uri, int depth, Map<String, String> data) {
        this.tag = tag;
        this.uri = uri;
        this.depth = depth;
        this.data = data;
    }

    public final String tag() {
        return this.tag;
    }

    public final URI uri() {
        return this.uri;
    }

    public final String baseUrl() {
        try {
            return URLUtil.getBase(uri.toURL()).toString();
        } catch (MalformedURLException e) {
            return uri.toString();
        }
    }

    public final Map<String, String> data() {
        return Utils.immutableMap(data);
    }

    public final int depth() {
        return depth;
    }

    public Seed fill(Map<String, String> data) {
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                fill(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public Seed fill(String key, String value) {
        if (key != null && value != null) {
            data.put(key, value);
        }
        return this;
    }

    public Seed fillIfAbsent(Map<String, String> data) {
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                fillIfAbsent(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public Seed fillIfAbsent(String key, String value) {
        if (key != null && value != null && !data.containsKey(key)) {
            data.put(key, value);
        }
        return this;
    }

    public final Scrap newScrapWithFill(String key, String value) {
        if (key == null || value == null) {
            throw new NullPointerException("key or value is null");
        }
        return new Scrap(tag, uri, depth + 1, new HashMap<>(data)).fill(key, value);
    }

    public final Scrap newScrapWithFill(Map<String, String> data) {
        if (data == null) {
            throw new NullPointerException("data == null");
        }
        return new Scrap(tag, uri, depth + 1, new HashMap<>(this.data)).fill(data);
    }

    public final Scrap newScrap(String uri) {
        return newScrap(URI.create(uri));
    }

    public final Scrap newScrap(URI uri) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        return new Scrap(tag, uri, depth + 1, new HashMap<>(data));
    }

    public final Scrap newScrapWithJoin(String uri) {
        return newScrapWithJoin(URI.create(uri));
    }

    public final Scrap newScrapWithJoin(URI uri) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        URI newUri = this.uri.resolve(uri);
        return new Scrap(tag, newUri, depth + 1, new HashMap<>(data));
    }

    @Override
    public String toString() {
        return "Seed{" +
                "tag='" + tag + '\'' +
                ", uri=" + uri +
                ", depth=" + depth +
                ", data=" + data +
                '}';
    }
}
