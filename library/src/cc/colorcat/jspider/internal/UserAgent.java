package cc.colorcat.jspider.internal;

/**
 * Created by cxx on 2017/11/11.
 * xx.ch@outlook.com
 */
public class UserAgent {
    public static final String NAME = "User-Agent";

    public static class Value {
        public static final String CHROME_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36";
        public static final String CHROME_WIN = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36";
        public static final String EDGE_WIN = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 Edge/16.16299";
        public static final String FIREFOX_WIN = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0";
        public static final String FIREFOX_LINUX = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36";
    }

    private UserAgent() {
        throw new AssertionError("no instance");
    }
}
