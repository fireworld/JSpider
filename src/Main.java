import java.net.MalformedURLException;
import java.util.*;

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

//        Scrap<String> scrap = new Scrap.Builder<String>(URI.create("https://www.coolapk.com/apk/"))
//                .registerParser(new CoolapkParser())
//                .listener(new Listener<String>() {
//                    @Override
//                    public void onSuccess(String data) {
//                        System.out.println("onSuccess, data = " + data);
//                    }
//
//                    @Override
//                    public void onFailure(Scrap<? extends String> scrap) {
//                        System.out.println("onFailure, scrap = " + scrap);
//                    }
//                }).build();
//        new JSpider().seed(Arrays.asList(scrap));
        test();
    }


    private static void test() {
//        List<String> ls = new ArrayList<>();
//        List<CharSequence> lc = new ArrayList<>();
//        Class c1 = ls.getClass();
//        Class c2 = lc.getClass();
//        System.out.println(c1);
//        System.out.println(c2);
//        System.out.println(c1 == c2);
//        System.out.println(c1.getTypeName());
//        Class c3 = List.class;
//        System.out.println(c3);
//        System.out.println(c3.isAssignableFrom(c1));
        Map<String, String> m1 = new LinkedHashMap<>();
        m1.put("2", "test2");
        m1.put("1", "test1");
        System.out.println("original m1: " + m1);
        Map<String, String> m2 = new LinkedHashMap<>();
        m2.put("2", "test22");
        m2.put("3", "test23");
        System.out.println("original m2: " + m2);
        m1.putAll(m2);
        System.out.println("after put all, m1: " + m1);
        System.out.println("after put all, m2: " + m2);
        Map<String, String> hash = new HashMap<>();
        hash.put("2", "hash2");
        hash.put("1", "hash1");
        System.out.println(hash);
    }
}
