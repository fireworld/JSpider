import cc.colorcat.spider.*;

import java.util.Collections;
import java.util.List;

/**
 * Created by cxx on 2017/11/14.
 * xx.ch@outlook.com
 */
public class SinaScoreRanking {
    public static final String TAG = "jfb";
    public static final String HOST = "http://sports.sina.com.cn";

    public static class Parser implements cc.colorcat.spider.Parser {
        @Override
        public List<Scrap> parse(Seed seed, WebSnapshot snapshot) {
            if (filter(seed)) {

            }

            return Collections.emptyList();
        }
    }

    public static class Handler implements cc.colorcat.spider.Handler {
        @Override
        public boolean handle(Scrap scrap) {
            if (filter(scrap)) {
                
            }
            return false;
        }
    }

    private static boolean filter(Seed seed) {
        return TAG.equals(seed.tag()) && HOST.equals(seed.uri().getHost());
    }
}
