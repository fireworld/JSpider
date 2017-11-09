package cc.colorcat.spider;

import cc.colorcat.spider.internal.Utils;

import java.util.List;
import java.util.Map;

public class JSpider {
    private final Map<String, List<Handler>> handlers;
    private final List<Interceptor> interceptors;
    private final Connection connection;
    private final Dispatcher dispatcher;
    private final boolean depthFirst;

    private JSpider(Builder builder) {
        this.handlers = Utils.immutableMap(builder.handlers);
        this.interceptors = Utils.immutableList(builder.interceptors);
        this.connection = builder.connection;
        this.dispatcher = builder.dispatcher;
        this.depthFirst = builder.depthFirst;
    }

    List<Handler> handlers(String tag) {
//        List<Handler> handlers = this.handlers.get(tag);
        return Utils.immutableList(handlers.get(tag));
    }

    List<Interceptor> interceptors() {
        return interceptors;
    }

    Connection connection() {
        return connection;
    }

    Dispatcher dispatcher() {
        return dispatcher;
    }

    boolean depthFirst() {
        return depthFirst;
    }

    int maxRetry() {
        return 3;
    }

    int maxSeedOnRunning() {
        return 20;
    }

    public static class Builder {
        private Map<String, List<Handler>> handlers;
        private List<Interceptor> interceptors;
        private Connection connection;
        private Dispatcher dispatcher;
        private boolean depthFirst = false;
    }
}
