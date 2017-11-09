import cc.colorcat.spider.Parser;
import cc.colorcat.spider.Scrap;
import cc.colorcat.spider.WebSnapshot;
import cc.colorcat.spider.internal.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestPaserNext implements Parser {

    @Override
    public List<Scrap> parse(Scrap seed, WebSnapshot snapshot) {
        if (!seed.tag().equals("image") || !seed.uri().toString().contains("http://www.mmjpg.com/")) {
            return Collections.emptyList();
        }
        Document doc = Jsoup.parse(snapshot.resource());
        Element e = doc.getElementById("page");
        if (e != null) {
            Elements elements = e.select("a[href~=^(/)(.)*[0-9]$]");
            int size = elements.size();
            List<Scrap> newScraps = new ArrayList<>(size);
            for (Element element : elements) {
                if (Utils.isEmpty(element.className())) {
                    newScraps.add(seed.newScrapWithJoin(element.attr("href")));
                }
            }
            return newScraps;
        }
        return Collections.emptyList();
    }
}
