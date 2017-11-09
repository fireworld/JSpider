import cc.colorcat.spider.*;
import cc.colorcat.spider.EventListener;
import cc.colorcat.spider.internal.Log;
import cc.colorcat.spider.internal.Utils;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Main {
    public static final CookieJar COOKIE_JAR;
    public static final OkHttpClient CLIENT;
    public static final JSpider SPIDER;
    public static final File SAVE_DIR;


    static {
        SAVE_DIR = new File("/Users/cxx/Pictures/spider");

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
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();

        SPIDER = new JSpider.Builder()
                .addParser(new TestPaserNext())
                .addParser(new TestParserImage())
                .registerHandler("image", new ImageHandler(new Downloader(CLIENT), SAVE_DIR))
                .eventListener(new LogListener())
                .connection(new OkConnection(CLIENT))
                .build();
    }

    public static void main(String[] args) throws IOException {
        String url = "http://www.mmjpg.com/mm/302";
        Map<String, String> def = new HashMap<>();
        def.put("dir", "mm");
        SPIDER.start(Scrap.newScraps("image", Arrays.asList(url), def));
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
        public void onCrawlStart(List<Scrap> seeds) {
            Log.i("onCrawlStart, seeds = " + seeds.toString());
        }

        @Override
        public void onCrawlSuccess(Scrap scrap) {
            Log.i("onCrawlSuccess, scrap = " + scrap.toString());
        }

        @Override
        public void onCrawlFailed(Scrap scrap) {
            Log.w("onCrawlFailed, scrap = " + scrap.toString());
        }

        @Override
        public void onCrawledData(Scrap data) {
            Log.i("onCrawledData, data = " + data.toString());
        }

        @Override
        public void onCrawlFinished(List<Scrap> all, List<Scrap> failed) {
            Log.i("onCrawlFinished, all = " + all.toString());
            Log.w("onCrawlFinished, failed = " + failed);
        }
    }
}
