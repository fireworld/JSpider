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
                downloader.submit(url, makePath(folderName, fileName));
                return true;
            }
            return false;
        }

        private File makePath(String folderName, String fileName) {
            File folder = directory;
            if (folderName != null && !folderName.isEmpty()) {
                folder = new File(folder, folderName);
            }
            if (folder.exists() || folder.mkdirs()) {
                String baseName, extName;
                int dot = fileName.lastIndexOf('.');
                if (dot != -1) {
                    baseName = fileName.substring(0, dot);
                    extName = fileName.substring(dot, fileName.length());
                } else {
                    baseName = fileName;
                    extName = "";
                }
                File savePath = new File(folder, baseName + extName);
                for (int i = 0; savePath.exists(); i++) {
                    savePath = new File(folder, baseName + "_" + i + extName);
                }
                return savePath;
            }
            throw new RuntimeException("create directory failed, path = " + folder.toString());
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
            Log("AllSeeds", allSeeds);
            Log("failedSeeds", failedSeeds);
            Log("handledScraps", handledScraps);
        }

        private static void Log(String tag, List<Scrap> scraps) {
            System.out.println("------------------------------- " + tag + " -------------------------------");
            for (Scrap scrap : scraps) {
                System.out.println(scrap);
            }
            System.out.println("----------------------------------------------------------------------");
        }
    }
}
