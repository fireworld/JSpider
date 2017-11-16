package cc.colorcat.jspider.test;

import cc.colorcat.jspider.Scrap;
import cc.colorcat.jspider.Seed;
import cc.colorcat.jspider.WebSnapshot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Created by cxx on 2017/11/14.
 * xx.ch@outlook.com
 */
public class SinaScoreRanking {
    static final String TAG = "jfb";
    private static final String HOST = "sports.sina.com.cn";

    public static class Parser implements cc.colorcat.jspider.Parser {
        @Override
        public List<Scrap> parse(Seed seed, WebSnapshot snapshot) {
            if (filter(seed)) {
                List<Scrap> scraps = new LinkedList<>();
                Document doc = Jsoup.parse(snapshot.resource(), seed.baseUrl());
                Element jfb = doc.getElementById("jfb");
                Scrap header = parseTableHeader(seed, jfb);
                if (header != null) {
                    scraps.add(header);
                }
                scraps.addAll(parseScoreRanking(seed, jfb));
                return scraps;
            }
            return Collections.emptyList();
        }

        private static Scrap parseTableHeader(Seed seed, Element jfb) {
            Element bg = jfb.selectFirst("tr.bg");
            if (bg != null) {
                Elements elements = bg.getElementsByTag("span");
                Map<String, String> map = new HashMap<>();
                for (int i = 0, size = elements.size(); i < size; i++) {
                    map.put(Integer.toString(i), elements.get(i).text());
                }
                return seed.newScrapWithFill(map);
            }
            return null;
        }

        private static List<Scrap> parseScoreRanking(Seed seed, Element jfb) {
            Elements trs = jfb.getElementsByTag("tr");
            if (trs.get(0).hasClass("bg")) trs.remove(0);
            List<Scrap> scraps = new LinkedList<>();
            for (Element tr : trs) {
                Elements td = tr.getElementsByTag("td");
                Map<String, String> map = new HashMap<>();
                for (int i = 0, size = td.size(); i < size; i++) {
                    if (i == 0 || i == 1) {
                        String text = td.get(i).child(0).text();
                        map.put(String.valueOf(i), text);
                    } else {
                        map.put(String.valueOf(i), td.get(i).text());
                    }
                }
                scraps.add(seed.newScrapWithFill(map));
            }
            return scraps;
        }
    }

    public static class Handler implements cc.colorcat.jspider.Handler {
        @Override
        public boolean handle(Scrap scrap) {
            if (filter(scrap)) {
                Map<String, String> data = scrap.data();
                StringBuilder sb = new StringBuilder();
                for (int i = 0, size = data.size(); i < size; i++) {
                    if (i != 0) sb.append("\t");
                    sb.append(data.get(String.valueOf(i)));
                }
                System.out.println(sb.toString());
                return true;
            }
            return false;
        }
    }

    private static boolean filter(Seed seed) {
        return TAG.equals(seed.tag()) && HOST.equals(seed.uri().getHost());
    }
}
