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

public class TestParserImage implements Parser {
    @Override
    public List<Scrap> parse(Scrap seed, WebSnapshot snapshot) {
        if (!seed.tag().equals("image") || !seed.uri().toString().contains("http://www.mmjpg.com/")) {
            return Collections.emptyList();
        }
        Document doc = Jsoup.parse(snapshot.resource());
        Element e = doc.getElementById("content");
        if (e != null) {
            Elements elements = e.select("img[src~=^(http)(s)?://(.)*\\.(jpg|png|jpeg)$]");
            int size = elements.size();
            List<Scrap> result = new ArrayList<>(size);
            for (Element element : elements) {
                result.add(seed.newScrapWithFill("url", element.attr("src")));
            }
            return result;
        }
        return Collections.emptyList();
    }
}
