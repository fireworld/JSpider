package cc.colorcat.spider;

import cc.colorcat.spider.internal.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by cxx on 17-11-11.
 * xx.ch@outlook.com
 */
final class HandlerInterceptor implements Interceptor {
    private final JSpider spider;

    HandlerInterceptor(JSpider spider) {
        this.spider = spider;
    }

    @Override
    public List<Scrap> intercept(Chain chain) throws IOException {
        List<Scrap> scraps = chain.proceed(chain.seed());
        Iterator<Scrap> iterator = scraps.iterator();
        while (iterator.hasNext()) {
            Scrap scrap = iterator.next();
            if (scrap.data().isEmpty()) continue;
            boolean handled = false;
            for (Handler handler : handlers(scrap)) {
                if (handler.handle(scrap)) {
                    handled = true;
                }
            }
            if (handled) {
                iterator.remove();
                spider.dispatcher().handled(scrap);
            }
        }
        return scraps;
    }

    private List<Handler> handlers(Scrap scrap) {
        return Utils.nullElse(spider.handlers.get(scrap.tag()), Collections.emptyList());
    }
}
