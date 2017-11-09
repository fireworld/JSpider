package cc.colorcat.spider.internal;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

public final class Utils {
    public static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String REG_HTTP_URL = "^(http)(s)?://(.)+";

    public static <K, V> Map<K, V> immutableMap(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap<>(map));
    }

    public static <T> List<T> immutableList(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    public static boolean isHttpUrl(String url) {
        return url != null && url.toLowerCase().matches(REG_HTTP_URL);
    }

    public static boolean isHttpUrl(URI uri) {
        if (uri == null) return false;
        String scheme = uri.getScheme();
        return scheme != null && scheme.toLowerCase().startsWith("http");
    }

    public static String urlJoin(String base, String url) {
        if (url.charAt(0) == '/') {
            return fetchRelativeUrl(base, true) + url;
        } else {
            return fetchRelativeUrl(base, false) + '/' + url;
        }
    }

    private static String fetchRelativeUrl(String url, boolean root) {
        int limit = url.charAt(4) == 's' ? 8 : 7;
        int index = root ? url.indexOf('/', limit) : url.lastIndexOf('/');
        if (index >= limit) {
            return url.substring(0, index);
        } else {
            return url;
        }
    }

    public static <T> T nullElse(T value, T other) {
        return value != null ? value : other;
    }

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignore) {

            }
        }
    }

    private Utils() {
        throw new AssertionError("no instance");
    }
}
