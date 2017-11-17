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
 * Created by cxx on 2017-11-16.
 * xx.ch@outlook.com
 */
public class HdWallpaper {
    private static final String TAG = "image";
    private static final String HOST = "www.hdwallpapers.in";

    public static class Parser implements cc.colorcat.jspider.Parser {
        @Override
        public List<Scrap> parse(Seed seed, WebSnapshot snapshot) {
            if (filter(seed)) {
                List<Scrap> scraps = new LinkedList<>();

                // find image's detail page
                Document doc = Jsoup.parse(snapshot.resource(), seed.baseUrl());
                Elements detail = doc.select("ul.wallpapers a[href$=.html]");
                detail.forEach(e -> scraps.add(seed.newScrapWithJoin(e.attr("href"))));

                // find next page
                Elements next = doc.select("div.pagination > span.selected + a[href^=/]");
                next.forEach(e -> scraps.add(seed.newScrapWithJoin(e.attr("href"))));

                // find image url
                Elements images = doc.select("div.thumbbg1 a[href~=^(/)(.)*\\.(jpg|png|jpeg)][target=_blank]");
                images.forEach(e -> scraps.add(seed.newScrapWithFill("url", seed.newUriWithJoin(e.attr("href")))));
                return scraps;
            }
            return Collections.emptyList();
        }
    }

    private static boolean filter(Seed seed) {
        return TAG.equals(seed.tag()) && HOST.equals(seed.uri().getHost());
    }
}
