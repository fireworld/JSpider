package cc.colorcat.jspider.test;

import cc.colorcat.jspider.JSpider;
import cc.colorcat.jspider.test.download.DownloadManager;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by cxx on 2017-11-9.
 * xx.ch@outlook.com
 */
public class Main {
    private static final CookieJar COOKIE_JAR;
    private static final OkHttpClient CLIENT;
    private static final JSpider SPIDER;
    public static final File SAVE_DIR;
    private static final DownloadManager MANAGER;

    static {
        SAVE_DIR = new File("/Users/cxx/Pictures/Spider");

        COOKIE_JAR = new CookieJar() {
            private Map<String, List<Cookie>> cookies = new ConcurrentHashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                this.cookies.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = this.cookies.get(url.host());
                return cookies != null ? cookies : Collections.emptyList();
            }
        };

        CLIENT = new OkHttpClient.Builder()
                .cookieJar(COOKIE_JAR)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        MANAGER = DownloadManager.create(new OkDownloader(CLIENT), 3);

        SPIDER = new JSpider.Builder()
                .addParser(new BingPaper.Parser())
                .addParser(new SinaScoreRanking.Parser())
                .addParser(new HdWallpaper.Parser())
                .addParser(new ZhuoKuPaper.Parser())
                .registerHandler("image", new ImageHandler(MANAGER, SAVE_DIR))
                .registerHandler("image", new BingPaper.Handler(MANAGER, SAVE_DIR))
                .registerHandler(SinaScoreRanking.TAG, new SinaScoreRanking.Handler())
                .eventListener(new TestEventListener())
                .connection(new OkConnection(CLIENT))
                .seedJar(new TestSeedJar())
                .maxDepth(100)
                .build();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input uri: ");
        String uri = scanner.next();
        System.out.print("Input folder name: ");
        String folder = scanner.next();
        testJSpider(uri.trim(), folder);
    }

    private static void testJSpider(String url, String folder) {
//        if (url.trim().toLowerCase().equals("q")) {
//            SPIDER.startWithSeedJar(Collections.emptyList());
//        } else {
//            SPIDER.startWithSeedJar(Collections.singletonList(Seed.newSeed("image", url)));
//        }
        Map<String, String> map;
        if (folder.trim().toLowerCase().equals("q")) {
            map = Collections.emptyMap();
        } else {
            map = new HashMap<>();
            map.put("dir", folder);
        }
        SPIDER.start("image", url, map);
//        SPIDER.start("image", "https://bing.ioliu.cn/ranking", def);
//        SPIDER.restartWithSeedJar();
    }
}
