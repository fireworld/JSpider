import cc.colorcat.jspider.Scrap;
import cc.colorcat.jspider.Seed;
import cc.colorcat.jspider.WebSnapshot;
import cc.colorcat.jspider.internal.Utils;
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
class BingPaper {
    private static final String TAG = "image";
    private static final String HOST = "bing.ioliu.cn";

    public static class Parser implements cc.colorcat.jspider.Parser {
        @Override
        public List<Scrap> parse(Seed seed, WebSnapshot snapshot) {
            if (filter(seed)) {
                List<Scrap> scraps = new LinkedList<>();
                Document doc = Jsoup.parse(snapshot.resource(), seed.baseUrl());
                Elements elements = doc.select("a[class='ctrl download'][href~=^(/photo/)(.)*(force=download)$][target='_blank'][rel=nofollow]");
                for (Element element : elements) {
                    String href = element.attr("href");
                    String url = seed.newUriWithJoin(href);
                    Scrap scrap = seed.newScrapWithFill("url", url);
                    scraps.add(scrap);
                }
                Element element = doc.select("a[href~=/(.)*\\?p=(\\d)+]").last();
                if (element != null) {
                    String nextSubUrl = element.attr("href");
                    scraps.add(seed.newScrapWithJoin(nextSubUrl));
                }
                return scraps;
            }
            return Collections.emptyList();
        }
    }

    public static class Handler implements cc.colorcat.jspider.Handler {
        private final DownloadManager manager;
        private final File directory;

        Handler(DownloadManager manager, File directory) {
            this.manager = manager;
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
                    manager.download(url, Utils.createSavePath(directory, folderName, fileName));
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
