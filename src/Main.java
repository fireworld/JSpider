import cc.colorcat.jspider.JSpider;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.BrowserVersionFeatures;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.html5.BrowserConnection;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private static final File SAVE_DIR;

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
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        SPIDER = new JSpider.Builder()
                .addParser(new BingPaper.Parser())
                .addParser(new SinaScoreRanking.Parser())
                .registerHandler("image", new ImageHandler(new DownloadManager(CLIENT), SAVE_DIR))
                .registerHandler("image", new BingPaper.Handler(new DownloadManager(CLIENT), SAVE_DIR))
                .registerHandler(SinaScoreRanking.TAG, new SinaScoreRanking.Handler())
                .eventListener(new TestEventListener())
                .connection(new OkConnection(CLIENT))
                .seedJar(new TestSeedJar())
                .maxDepth(2)
                .build();
    }

    public static void main(String[] args) throws IOException {
        String url = "http://sports.sina.com.cn/g/pl/table.html";
        HtmlUnitDriver driver = createDriver();
        driver.get(url);
        System.out.println(driver.getPageSource());

//        WebClient client = createWebClient();
//        Page page = client.getPage(url);
//        System.out.println(page.getWebResponse().getContentAsString());
//        testJSpider();
    }

    private static void testJSpider() {
//        Map<String, String> def = new HashMap<>();
//        def.put("dir", "Bing");
//        SPIDER.start("image", "https://bing.ioliu.cn/", def);
//        SPIDER.start("image", "https://bing.ioliu.cn/ranking", def);
        SPIDER.start(SinaScoreRanking.TAG, "http://sports.sina.com.cn/g/pl/table.html");
    }

    private static WebClient createWebClient() {
        WebClient client = new WebClient(BrowserVersion.FIREFOX_3_6);
        client.setJavaScriptEnabled(true);
        client.setJavaScriptTimeout(10000);
        return client;
    }

    private static HtmlUnitDriver createDriver() {
        HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
        driver.setJavascriptEnabled(true);
        return driver;
    }
}
