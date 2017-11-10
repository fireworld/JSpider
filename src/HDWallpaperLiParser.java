import cc.colorcat.spider.Parser;
import cc.colorcat.spider.Scrap;
import cc.colorcat.spider.WebSnapshot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by cxx on 17-11-10.
 * xx.ch@outlook.com
 */
public class HDWallpaperLiParser implements Parser {
    public static final String HOST = "www.hdwallpapers.net";
    public static final String TAG = "image";

    @Override
    public List<Scrap> parse(Scrap seed, WebSnapshot snapshot) {
        if (!TAG.equals(seed.tag()) || !seed.uri().toString().contains(HOST)) {
            return Collections.emptyList();
        }
        Document doc = Jsoup.parse(snapshot.resource(), seed.baseUrl());
        Element element = doc.selectFirst("ul.'wallpapers uk-grid uk-grid-width-small-1-2 uk-grid-width-medium-1-2 uk-grid-width-large-1-3'");
        if (element != null) {
            Elements elements = element.select("a[href~=^(/)(.)*\\.(html|htm)$]");
            List<Scrap> scraps = new ArrayList<>(elements.size());
            for (Element e : elements) {
                Scrap scrap = seed.newScrapWithJoin(e.attr("href"));
                scraps.add(scrap);
            }
            return scraps;
        }
        return Collections.emptyList();
    }
}
