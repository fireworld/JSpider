package cc.colorcat.jspider.test;

import cc.colorcat.jspider.Scrap;
import cc.colorcat.jspider.Seed;
import cc.colorcat.jspider.WebSnapshot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxx on ${DATA}.
 * xx.ch@outlook.com
 */
public class ZhuoKuPaper {
    private static final String TAG = "image";
    private static final String HOST = "zhuoku.com";

    public static class Parser implements cc.colorcat.jspider.Parser {

        @Override
        public List<Scrap> parse(Seed seed, WebSnapshot snapshot) {
            if (filter(seed)) {
                List<Scrap> scraps = new LinkedList<>();
                Document doc = Jsoup.parse(snapshot.resource(), seed.baseUrl());

                Elements images = doc.select("img#imageview[src~=^(http)(s)?://(.)*\\.(jpg|png|jpeg)$]");
                images.forEach(e -> scraps.add(seed.newScrapWithFill("url", e.attr("src"))));

                Elements nextPages = doc.select("div#bizhi a[href$=.htm]");
                nextPages.forEach(e -> scraps.add(seed.newScrapWithJoin(e.attr("href"))));

                return scraps;
            }
            return Collections.emptyList();
        }
    }

    private static boolean filter(Seed seed) {
        return TAG.equals(seed.tag()) && seed.uri().getHost().contains(HOST);
    }
}
