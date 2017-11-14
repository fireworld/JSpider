package cc.colorcat.jspider;

import java.net.URI;
import java.util.Map;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public final class Scrap extends Seed {

    Scrap(String tag, URI uri, int depth, Map<String, String> data) {
        super(tag, uri, depth, data);
    }

    @Override
    public Scrap fill(Map<String, String> data) {
        super.fill(data);
        return this;
    }

    @Override
    public Scrap fill(String key, String value) {
        super.fill(key, value);
        return this;
    }

    @Override
    public Scrap fillIfAbsent(Map<String, String> data) {
        super.fillIfAbsent(data);
        return this;
    }

    @Override
    public Scrap fillIfAbsent(String key, String value) {
        super.fillIfAbsent(key, value);
        return this;
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
