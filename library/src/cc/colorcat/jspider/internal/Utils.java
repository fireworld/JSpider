package cc.colorcat.jspider.internal;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by cxx on 2017-11-9.
 * xx.ch@outlook.com
 */
public final class Utils {
    public static final Charset UTF8 = Charset.forName("UTF-8");

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

    public static boolean isHttpUrl(URI uri) {
        if (uri == null) return false;
        String scheme = uri.getScheme();
        return scheme != null && scheme.toLowerCase().matches("(http)(s)?");
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

    public static Charset parseCharset(String contentType, Charset defaultCharset) {
        if (contentType != null) {
            String[] params = contentType.split(";");
            final int length = params.length;
            for (int i = 1; i < length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equalsIgnoreCase("charset")) {
                        try {
                            return Charset.forName(pair[1]);
                        } catch (Exception ignore) {
                            return defaultCharset;
                        }
                    }
                }
            }
        }
        return defaultCharset;
    }

    public static <T> T nullElse(T value, T other) {
        return value != null ? value : other;
    }

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }

    public static String justReadString(InputStream is, Charset charset) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));
        char[] buffer = new char[1024];
        for (int length = br.read(buffer); length != -1; length = br.read(buffer)) {
            sb.append(buffer, 0, length);
        }
        return sb.toString();
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
