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

public class CoolapkParser implements Parser {
    @Override
    public List<Scrap> parse(Scrap seed, WebSnapshot snapshot) {
        String baseUrl = seed.baseUrl();
        System.out.println("baseUrl --> " + baseUrl);
        if (!seed.tag().equals("image")) return Collections.emptyList();
        Document doc = Jsoup.parse(snapshot.resource(), baseUrl);
        Elements elements = doc.select("img[src~=^(http)(s)?://(.)*\\.(jpg|png|jpeg)$]");
        List<Scrap> result = new ArrayList<>(elements.size());
        for (Element e : elements) {
            Scrap newScrap = seed.newScrap(seed.uri()).fill("url", e.attr("src"));
            result.add(newScrap);
        }
        return result;
    }
}
