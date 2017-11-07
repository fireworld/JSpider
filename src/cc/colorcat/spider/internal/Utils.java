package cc.colorcat.spider.internal;

public final class Utils {
    private static final String REG_HTTP_URL = "^(http)(s)?://(.)+";

    public static boolean isHttpUrl(String url) {
        return url != null && url.toLowerCase().matches(REG_HTTP_URL);
    }

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }

    public static String urlJoin(String base, String url) {
        if (url.charAt(0) == '/') {
            return fetchRelativeUrl(base, true) + url;
        } else {
            return fetchRelativeUrl(base, false) + '/' + url;
        }
    }

    public static String fetchRelativeUrl(String url, boolean root) {
        int limit = url.charAt(4) == 's' ? 8 : 7;
        int index = root ? url.indexOf('/', limit) : url.lastIndexOf('/');
        if (index >= limit) {
            return url.substring(0, index);
        } else {
            return url;
        }
    }

    private Utils() {
        throw new AssertionError("no instance");
    }
}
