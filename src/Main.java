import cc.colorcat.spider.Handler;
import cc.colorcat.spider.JSpider;
import cc.colorcat.spider.Scrap;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Main {
    public static final OkHttpClient CLIENT;
    public static final JSpider SPIDER;

    private static final Handler COOL_HANDLER = new Handler() {
        @Override
        public boolean handle(Scrap scrap) {
            Map<String, String> data = scrap.data();
            String url = data.get("url");
            if (url != null && url.matches("^(http)(s)?://(.)*\\.(jpg|png|jpeg)$")) {
                System.out.println("fetch img success, url = " + url);
                return true;
            }
            return false;
        }
    };

    static {
        SPIDER = new JSpider.Builder()
                .addParser(new CoolapkParser())
                .registerHandler("cool", COOL_HANDLER)
                .build();

        CLIENT = new OkHttpClient();
    }

    public static void main(String[] args) throws IOException {
        String url = "https://www.coolapk.com/apk/";
        SPIDER.start(Scrap.newScraps("cool", Arrays.asList(url)));
//        String resource = CLIENT.newCall(new Request.Builder().url(url).get().build()).execute().body().string();
//        Document doc = Jsoup.parse(resource);
//        Elements elements = doc.select("img[src~=^(http)(s)?://(.)*\\.(jpg|png|jpeg)$]");
//        for (Element e : elements) {
//            System.out.println("jsoup success, img = " + e.attr("src"));
//        }
    }


    private static void test() {
//        List<String> ls = new ArrayList<>();
//        List<CharSequence> lc = new ArrayList<>();
//        Class c1 = ls.getClass();
//        Class c2 = lc.getClass();
//        System.out.println(c1);
//        System.out.println(c2);
//        System.out.println(c1 == c2);
//        System.out.println(c1.getTypeName());
//        Class c3 = List.class;
//        System.out.println(c3);
//        System.out.println(c3.isAssignableFrom(c1));
        Map<String, String> m1 = new LinkedHashMap<>();
        m1.put("2", "test2");
        m1.put("1", "test1");
        System.out.println("original m1: " + m1);
        Map<String, String> m2 = new LinkedHashMap<>();
        m2.put("2", "test22");
        m2.put("3", "test23");
        System.out.println("original m2: " + m2);
        m1.putAll(m2);
        System.out.println("after put all, m1: " + m1);
        System.out.println("after put all, m2: " + m2);
        Map<String, String> hash = new HashMap<>();
        hash.put("2", "hash2");
        hash.put("1", "hash1");
        System.out.println(hash);
    }
}
