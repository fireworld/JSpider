package cc.colorcat.spider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;


public class CoolapkParser implements Parser<String> {

    @Override
    public List<Scrap<? extends String>> parse(Scrap<String> scrap, WebSnapshot snapshot) {
        List<Scrap<? extends String>> result = new LinkedList<>();
        Document doc = Jsoup.parse(snapshot.resource());
        Elements es = doc.select("img[src~=^(http)(s)?://(.)*\\.(png|jpg|jpeg)$]");
        for (Element e : es) {
            String imgUrl = e.attr("src");
            result.add(scrap.newBuilder().data(scrap.uri().resolve(imgUrl).toString()).build());
        }
        return result;
    }
}
