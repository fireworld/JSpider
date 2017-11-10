import cc.colorcat.spider.*;
import cc.colorcat.spider.internal.Log;
import cc.colorcat.spider.internal.Utils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public class Main {
    public static final CookieJar COOKIE_JAR;
    public static final OkHttpClient CLIENT;
    public static final JSpider SPIDER;
    public static final File SAVE_DIR;


    static {
        SAVE_DIR = new File("D:\\temp");

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

        SPIDER = new JSpider.Builder()
                .addParser(new CoolapkParser())
                .registerHandler("image", new ImageHandler(new Downloader(CLIENT), SAVE_DIR))
                .eventListener(new LogListener())
                .connection(new OkConnection(CLIENT))
                .build();
    }

    public static void main(String[] args) throws IOException {
        testJSpider();
//        testJsoup();
    }

    private static void testJSpider() {
        Map<String, String> def = new HashMap<>();
        def.put("dir", "testImage");
        SPIDER.start("image", "https://www.coolapk.com/apk/", def);
    }

    private static void testJsoup() throws IOException {
        Document doc = Jsoup.connect("https://www.coolapk.com/apk/").get();
        Elements elements = doc.select("img[src~=^(http)(s)?://(.)*\\.(jpg|png|jpeg)$]");
        for (Element element : elements) {
            System.out.println(element.attr("src"));
        }
    }

    private static class ImageHandler implements Handler {
        private Downloader downloader;
        private File directory;


        private ImageHandler(Downloader downloader, File directory) {
            this.downloader = downloader;
            this.directory = directory;
        }

        @Override

        public boolean handle(Scrap scrap) {
            Map<String, String> data = scrap.data();
            String url = data.get("url");
            if (url != null && url.matches("^(http)(s)?://(.)*\\.(jpg|png|jpeg)$")) {
                String name = url.substring(url.lastIndexOf('/'), url.length());
                String dirS = data.get("dir");
                File savePath = directory;
                if (!Utils.isEmpty(dirS)) {
                    savePath = new File(savePath, dirS);
                }
                savePath = new File(savePath, name);
                int count = 1;
                while (savePath.exists()) {
                    savePath = new File(directory, count + "_" + name);
                }
                downloader.submit(url, savePath);
                return true;
            }
            return false;
        }

    }

    private static class LogListener implements EventListener {
        @Override
        public void onStart(List<Scrap> seeds) {
//            Log.i("onStart, seeds = " + seeds.toString());
        }

        @Override
        public void onSuccess(Scrap seed) {
//            Log.i("onSuccess, seed = " + seed.toString());
        }

        @Override
        public void onFailed(Scrap seed) {
//            Log.w("onFailed, seed = " + seed.toString());
        }

        @Override
        public void onHandled(Scrap scrap) {
//            Log.i("onHandled, scrap = " + scrap.toString());
        }

        @Override
        public void onFinished(List<Scrap> allSeeds, List<Scrap> failedSeeds, List<Scrap> handledScraps) {
            Log.i("onFinished, all = " + allSeeds.toString());
            Log.w("onFinished, failed = " + failedSeeds.toString());
            Log.i("onFinished, handled = " + handledScraps.toString());
        }
    }
}
