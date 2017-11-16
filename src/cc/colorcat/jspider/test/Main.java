package cc.colorcat.jspider.test;

import cc.colorcat.jspider.JSpider;
import cc.colorcat.jspider.test.download.DownloadManager;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public class Main {
    private static final CookieJar COOKIE_JAR;
    private static final OkHttpClient CLIENT;
    private static final JSpider SPIDER;
    public static final File SAVE_DIR;
    private static final DownloadManager MANAGER;

    static {
        SAVE_DIR = new File("/home/cxx/图片/Spider/hd");

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
                .registerHandler("image", new ImageHandler(MANAGER, SAVE_DIR))
                .registerHandler("image", new BingPaper.Handler(MANAGER, SAVE_DIR))
                .registerHandler(SinaScoreRanking.TAG, new SinaScoreRanking.Handler())
                .eventListener(new TestEventListener())
                .connection(new OkConnection(CLIENT))
                .seedJar(new TestSeedJar())
                .maxDepth(1)
                .build();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input uri: ");
        String uri = scanner.next();
        testJSpider(uri.trim());
//        System.out.println(System.getProperties());
//        Document doc = Jsoup.connect("https://www.hdwallpapers.in/lionel_messi_fc_barcelona_hd_4k-wallpapers.html").get();
//        Elements elements = doc.select("div.pagination > span.selected + a[href^=/]");
//        Elements images = doc.select("div.thumbbg1 a[href~=^(/)(.)*\\.(jpg|png|jpeg)][target=_blank]");
//        images.forEach(e -> System.out.println(e.attr("href")));
    }

    private static void testJSpider(String url) {
        SPIDER.start("image", url);
//        SPIDER.start("image", "https://bing.ioliu.cn/ranking", def);
//        SPIDER.restartWithSeedJar();
    }
}
