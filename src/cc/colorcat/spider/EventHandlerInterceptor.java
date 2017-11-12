package cc.colorcat.spider;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by cxx on 17-11-11.
 * xx.ch@outlook.com
 */
final class EventHandlerInterceptor implements Interceptor {
    private final Dispatcher dispatcher;
    private final Map<String, List<Handler>> handlers;
    private final EventListener listener;

    EventHandlerInterceptor(Dispatcher dispatcher, Map<String, List<Handler>> handlers, EventListener listener) {
        this.dispatcher = dispatcher;
        this.handlers = handlers;
        this.listener = listener;
    }

    @Override
    public List<Scrap> intercept(Chain chain) throws IOException {
        List<Scrap> scraps = new LinkedList<>();
        try {
            scraps.addAll(chain.proceed(chain.seed()));

        } catch (IOException e) {

        } finally {

        }
        return scraps;
    }
}
