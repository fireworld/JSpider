package cc.colorcat.spider;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cxx on 2017/11/13.
 * xx.ch@outlook.com
 */
final class SeedsCleanerInterceptor implements Interceptor {
    private final JSpider spider;

    SeedsCleanerInterceptor(JSpider spider) {
        this.spider = spider;
    }

    @Override
    public List<Scrap> intercept(Chain chain) throws IOException {
        List<Scrap> scraps = chain.proceed(chain.seed());
        Iterator<Scrap> iterator = scraps.iterator();
        while (iterator.hasNext()) {
            Scrap scrap = iterator.next();
            if (scrap.depth() > spider.maxDepth()) {
                iterator.remove();
                spider.dispatcher().onReachedMaxDepth(scrap);
            }
        }
        return scraps;
    }
}
