import cc.colorcat.spider.*;
import cc.colorcat.spider.internal.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by cxx on ${DATA}.
 * xx.ch@outlook.com
 */
public class BingPaper {
    private static final String TAG = "image";
    private static final String HOST = "bing.ioliu.cn";

    public static class BingParser implements Parser {
        @Override
        public List<Scrap> parse(Seed seed, WebSnapshot snapshot) {
            if (filter(seed)) {
                List<Scrap> scraps = new LinkedList<>();
                Document doc = Jsoup.parse(snapshot.resource(), seed.baseUrl());
                Elements elements = doc.select("a[class='ctrl download'][href~=^(/photo/)(.)*(force=download)$][target='_blank'][rel=nofollow]");
                for (Element element : elements) {
                    String href = element.attr("href");
                    String url = seed.uri().resolve(href).toString();
                    Scrap scrap = seed.newScrapWithFill("url", url);
                    scraps.add(scrap);
                }
                Element element = doc.selectFirst("a[href~=/\\?p=(\\d)+]");
                String nextSubUrl = element.attr("href");
                scraps.add(seed.newScrapWithJoin(nextSubUrl));
                return scraps;
            }
            return Collections.emptyList();
        }
    }

    public static class BingHandler implements Handler {
        private final Downloader downloader;
        private final File directory;

        public BingHandler(Downloader downloader, File directory) {
            this.downloader = downloader;
            this.directory = directory;
        }

        @Override
        public boolean handle(Scrap scrap) {
            if (filter(scrap)) {
                Map<String, String> data = scrap.data();
                String url = data.get("url");
                if (url != null && url.matches("^(http)(s)?://(.)*(force=download)$")) {
                    String folderName = Utils.nullElse(data.get("dir"), "Bing");
                    String fileName;
                    int startIndex = url.lastIndexOf('/') + 1;
                    int queryIndex = url.indexOf('?');
                    if (queryIndex != -1) {
                        fileName = url.substring(startIndex, queryIndex) + ".jpg";
                    } else {
                        fileName = System.nanoTime() + ".jpg";
                    }
                    downloader.submit(url, Utils.createSavePath(directory, folderName, fileName));
                    return true;
                }
            }
            return false;
        }
    }

    private static boolean filter(Seed seed) {
        return TAG.equals(seed.tag()) && HOST.equals(seed.uri().getHost());
    }
}
