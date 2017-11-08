import cc.colorcat.spider.CoolapkParser;
import cc.colorcat.spider.JSpider;
import cc.colorcat.spider.Listener;
import cc.colorcat.spider.Scrap;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
//        String url = "http://www.baidu.com:80/test/tomato/search.html";
//        URI uri = URI.create(url);
//        String path = "../hello/world/test.png";
//        URI newUri = uri.resolve(path);
//        System.out.println("original uri = " + uri.toString());
//        System.out.println("new path = " + path);
//        System.out.println("new uri = " + newUri);

//        String baidu1 = "http://www.baidu.com";
//        String baidu2 = "HTTP://WWW.BAIDU.COM";
//        Scrap<String> s1 = new Scrap.Builder<String>(baidu1).build();
//        Scrap<String> s2 = new Scrap.Builder<String>(baidu2).data("haha").build();
//        ConcurrentSkipListSet<Scrap<?>> set = new ConcurrentSkipListSet<>(Comparator.comparing(Scrap::uri));
//        set.add(s1);
//        System.out.println(set.contains(s2));
//        set.add(s2);
//        System.out.println(set);
//        set.remove(s2);
//        System.out.println(set);

        Scrap<String> scrap = new Scrap.Builder<String>(URI.create("https://www.coolapk.com/apk/"))
                .registerParser(new CoolapkParser())
                .listener(new Listener<String>() {
                    @Override
                    public void onSuccess(String data) {
                        System.out.println("onSuccess, data = " + data);
                    }

                    @Override
                    public void onFailure(Scrap<? extends String> scrap) {
                        System.out.println("onFailure, scrap = " + scrap);
                    }
                }).build();
        new JSpider().seed(Arrays.asList(scrap));
    }
}
