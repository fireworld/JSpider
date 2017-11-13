import cc.colorcat.spider.*;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
        SAVE_DIR = new File("/home/cxx/图片/Temp");

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
//                .addParser(new CoolapkParser())
                .addParser(new BingPaper.BingParser())
                .registerHandler("image", new ImageHandler(new Downloader(CLIENT), SAVE_DIR))
                .registerHandler("image", new BingPaper.BingHandler(new Downloader(CLIENT), SAVE_DIR))
                .eventListener(new LogListener())
//                .connection(new OkConnection(CLIENT))
                .seedJar(new LogSeedJar())
                .maxDepth(3)
                .build();
    }

    public static void main(String[] args) throws IOException {
//        String path = "D:\\Workspace\\IdeaProjects\\JSpider\\src\\CoolapkParser.java";
//        InputStream is = new FileInputStream(path);
//        String s = Utils.toString(is, Utils.UTF8);
//        System.out.println(s);
        testJSpider();
//        String reg = "/\\?p=(\\d)+";
//        String text = "/?p=01";
//        System.out.println(text.matches(reg));
//        testJsoup();
//        testDownload("https://bing.ioliu.cn//photo/FreshSalt_ZH-CN12818759319?force=download");
//        String reg = "^(/photo/)(.)*(force=download)$";
//        String text = "/photo/KyrgyzstanCat_EN-AU10859527245?force=download";
//        System.out.println(text.matches(reg));
    }

    private static void testJSpider() {
        Map<String, String> def = new HashMap<>();
        def.put("dir", "Bing");
//        SPIDER.start("image", "https://www.coolapk.com/apk/", def);
        SPIDER.start("image", "https://bing.ioliu.cn/?p=1", def);
    }

    private static void testJsoup() throws IOException {
        Document doc = Jsoup.connect("https://www.coolapk.com/apk/").get();
        Elements elements = doc.select("img[src~=^(http)(s)?://(.)*\\.(jpg|png|jpeg)$]");
        for (Element element : elements) {
            System.out.println(element.attr("src"));
        }
    }

    private static void testDownload(String url) throws IOException {
        File path = new File(SAVE_DIR, System.currentTimeMillis() + ".jpg");
        new Downloader(CLIENT).download(url, path);
    }

    private static class ImageHandler implements Handler {
        private final Downloader downloader;
        private final File directory;


        private ImageHandler(Downloader downloader, File directory) {
            this.downloader = downloader;
            this.directory = directory;
        }

        @Override

        public boolean handle(Scrap scrap) {
            Map<String, String> data = scrap.data();
            String url = data.get("url");
            if (url != null && url.matches("^(http)(s)?://(.)*\\.(jpg|png|jpeg)$")) {
                String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
                String folderName = data.get("dir");
                downloader.submit(url, Utils.createSavePath(directory, folderName, fileName));
                return true;
            }
            return false;
        }
    }

    private static class LogListener implements EventListener {

        @Override
        public void onSuccess(Seed seed) {
//            Log.i("onSuccess, seed = " + seed.toString());
        }

        @Override
        public void onFailure(Seed seed, Exception reason) {
//            Log.w("onFailed, seed = " + seed.toString());
        }

        @Override
        public void onHandled(Scrap scrap) {
//            Log.i("onHandled, scrap = " + scrap.toString());
        }

        @Override
        public void onReachedMaxDepth(Seed seed) {
            Log("onReachedMaxDepth", Collections.singletonList(seed));
        }
    }

    private static class LogSeedJar implements SeedJar {
        @Override
        public void save(List<Seed> success, List<Seed> failed, List<Seed> reachedMaxDepth) {
            Log("success", success);
            Log("failed", failed);
            Log("reachedMaxDepth", reachedMaxDepth);
        }

        @Override
        public List<Seed> load() {
            return null;
        }
    }

    private static void Log(String tag, List<? extends Seed> seeds) {
        System.err.println(buildLine(tag, 50, true));
        if (seeds.isEmpty()) {
            System.err.println("empty");
        } else {
            for (Seed scrap : seeds) {
                System.err.println(scrap);
            }
        }
        System.err.println(buildLine(tag, 50, false));
    }

    private static String buildLine(String tag, int numOfHalf, boolean withTag) {
        StringBuilder result = new StringBuilder();
        String half = buildLine(numOfHalf);
        if (withTag) {
            result.append(half).append(' ').append(tag).append(' ').append(half);
        } else {
            result.append(half).append(buildLine(tag.length() + 2)).append(half).append('\n').append('\n');
        }
        return result.toString();
    }

    private static String buildLine(int num) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < num; i++) {
            line.append("-");
        }
        return line.toString();
    }
}
