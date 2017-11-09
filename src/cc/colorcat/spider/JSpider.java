package cc.colorcat.spider;

import cc.colorcat.spider.internal.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JSpider {
    private final Map<String, List<Handler>> handlers;
    private final List<Interceptor> interceptors;
    private final List<Parser> parsers;
    private final Connection connection;
    private final ExecutorService executor;
    private final Dispatcher dispatcher;
    private final boolean depthFirst;
    private final int maxRetry;
    private final int maxSeedOnRunning;

    private JSpider(Builder builder) {
        this.handlers = Utils.immutableMap(builder.handlers);
        this.interceptors = Utils.immutableList(builder.interceptors);
        this.parsers = Utils.immutableList(builder.parsers);
        this.connection = builder.connection;
        this.executor = builder.executor;
        this.dispatcher = builder.dispatcher != null ? builder.dispatcher : new Dispatcher(this, executor);
        this.depthFirst = builder.depthFirst;
        this.maxRetry = builder.maxRetry;
        this.maxSeedOnRunning = builder.maxSeedOnRunning;
    }

    ExecutorService executor() {
        return executor;
    }

    List<Parser> parsers() {
        return parsers;
    }

    List<Handler> handlers(String tag) {
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
        return maxRetry;
    }

    int maxSeedOnRunning() {
        return maxSeedOnRunning;
    }

    public void start(List<Scrap> scraps) {
        this.dispatcher.enqueue(scraps);
    }

    public static class Builder {
        private Map<String, List<Handler>> handlers = new HashMap<>();
        private List<Interceptor> interceptors = new ArrayList<>(6);
        private List<Parser> parsers = new ArrayList<>(16);
        private Connection connection = new OkConnection();
        private ExecutorService executor;
        private Dispatcher dispatcher;
        private boolean depthFirst = false;
        private int maxRetry = 3;
        private int maxSeedOnRunning = 20;

        public Builder() {

        }

        public Builder registerHandler(String tag, Handler handler) {
            if (Utils.isEmpty(tag)) {
                throw new IllegalArgumentException("tag is empty");
            }
            if (handler == null) {
                throw new NullPointerException("handler == null");
            }
            List<Handler> hs = this.handlers.computeIfAbsent(tag, k -> new ArrayList<>());
            if (!hs.contains(handler)) {
                hs.add(handler);
            }
            return this;
        }

        public Builder addParser(Parser parser) {
            if (parser == null) {
                throw new NullPointerException("parser == null");
            }
            if (!this.parsers.contains(parser)) {
                this.parsers.add(parser);
            }
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptor == null) {
                throw new NullPointerException("interceptor == null");
            }
            return this;
        }

        public JSpider build() {
            if (executor == null) {
                executor = Executors.newCachedThreadPool();
            }
            return new JSpider(this);
        }
    }
}
