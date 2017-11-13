package cc.colorcat.spider;

import cc.colorcat.spider.internal.Utils;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public class JSpider implements Call.Factory {
    final Map<String, List<Handler>> handlers;
    private final List<Interceptor> interceptors;
    private final List<Parser> parsers;
    final ParserProxy parserProxy;
    private final Connection connection;
    private final ExecutorService executor;
    private final Dispatcher dispatcher;
    private final boolean depthFirst;
    private final int maxTry;
    private final int maxSeedOnRunning;
    private final int maxDepth;
    private final EventListener listener;
    private final SeedJar seedJar;

    private JSpider(Builder builder) {
        this.handlers = Utils.immutableMap(builder.handlers);
        this.interceptors = Utils.immutableList(builder.interceptors);
        this.parsers = Utils.immutableList(builder.parsers);
        this.parserProxy = new ParserProxy(this.parsers);
        this.connection = builder.connection;
        this.executor = builder.executor;
        this.dispatcher = builder.dispatcher != null ? builder.dispatcher : new Dispatcher(this, executor);
        this.depthFirst = builder.depthFirst;
        this.maxTry = builder.maxTry;
        this.maxSeedOnRunning = builder.maxSeedOnRunning;
        this.maxDepth = builder.maxDepth;
        this.listener = builder.listener;
        this.seedJar = builder.seedJar;
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
        return connection.clone();
    }

    Dispatcher dispatcher() {
        return dispatcher;
    }

    boolean depthFirst() {
        return depthFirst;
    }

    int maxTry() {
        return maxTry;
    }

    int maxSeedOnRunning() {
        return maxSeedOnRunning;
    }

    int maxDepth() {
        return maxDepth;
    }

    EventListener listener() {
        return listener;
    }

    SeedJar seedJar() {
        return seedJar;
    }

    public void start(String tag, String uri) {
        start(tag, uri, Collections.emptyMap());
    }

    public void start(String tag, String uri, Map<String, String> defaultData) {
        start(tag, Collections.singletonList(uri), defaultData);
    }

    public void start(String tag, List<String> uris) {
        start(tag, uris, Collections.emptyMap());
    }

    public void start(String tag, List<String> uris, Map<String, String> defaultData) {
        List<Seed> seeds = Seed.newSeeds(tag, uris, defaultData);
        mapAndEnqueue(seeds);
//        listener.onStart(seeds);
    }

    void mapAndEnqueue(List<? extends Seed> seeds) {
        List<Call> calls = new ArrayList<>(seeds.size());
        for (Seed seed : seeds) {
            calls.add(newCall(seed));
        }
        dispatcher.enqueue(calls);
    }

    @Override
    public Call newCall(Seed seed) {
        return new RealCall(this, seed);
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        private Map<String, List<Handler>> handlers;
        private List<Interceptor> interceptors;
        private List<Parser> parsers;
        private Connection connection;
        private ExecutorService executor;
        private Dispatcher dispatcher;
        private boolean depthFirst = false;
        private int maxTry = 3;
        private int maxSeedOnRunning = 20;
        private int maxDepth = 100;
        private EventListener listener;
        private SeedJar seedJar;

        public Builder() {
            handlers = new HashMap<>();
            interceptors = new ArrayList<>();
            parsers = new ArrayList<>();
            listener = EventListener.EMPTY_LISTENER;
            seedJar = SeedJar.NO_SEEDS;
        }

        public Builder(JSpider spider) {
            this.handlers = new HashMap<>(spider.handlers);
            this.interceptors = new ArrayList<>(spider.interceptors);
            this.parsers = new ArrayList<>(spider.parsers);
            this.connection = spider.connection;
            this.executor = spider.executor;
            this.dispatcher = spider.dispatcher;
            this.maxTry = spider.maxTry;
            this.maxSeedOnRunning = spider.maxSeedOnRunning;
            this.maxDepth = spider.maxDepth;
            this.listener = spider.listener;
            this.seedJar = spider.seedJar;
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

        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptor == null) {
                throw new NullPointerException("interceptor == null");
            }
            if (!this.interceptors.contains(interceptor)) {
                this.interceptors.add(interceptor);
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

        public Builder connection(Connection connection) {
            if (connection == null) {
                throw new NullPointerException("connection == null");
            }
            this.connection = connection;
            return this;
        }

        public Builder executor(ExecutorService executor) {
            if (executor == null) {
                throw new NullPointerException("executor == null");
            }
            this.executor = executor;
            return this;
        }

        public Builder depthFirst(boolean depthFirst) {
            this.depthFirst = depthFirst;
            return this;
        }

        public Builder maxTry(int maxRetry) {
            if (maxRetry < 1) {
                throw new IllegalArgumentException("maxTry < 1");
            }
            this.maxTry = maxRetry;
            return this;
        }

        public Builder maxSeedOnRunning(int maxSeedOnRunning) {
            if (maxSeedOnRunning < 1) {
                throw new IllegalArgumentException("maxSeedOnRunning < 1");
            }
            this.maxSeedOnRunning = maxSeedOnRunning;
            return this;
        }

        public Builder maxDepth(int maxDepth) {
            if (maxDepth < 1) {
                throw new IllegalArgumentException("maxDepth < 1");
            }
            this.maxDepth = maxDepth;
            return this;
        }

        public Builder eventListener(EventListener listener) {
            this.listener = Utils.nullElse(listener, EventListener.EMPTY_LISTENER);
            return this;
        }

        public Builder seedJar(SeedJar seedJar) {
            this.seedJar = Utils.nullElse(seedJar, SeedJar.NO_SEEDS);
            return this;
        }

        public JSpider build() {
            if (connection == null) {
                connection = new HttpConnection();
            }
            if (executor == null) {
                executor = defaultService();
            }
            return new JSpider(this);
        }

        private static ExecutorService defaultService() {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 10, 60L, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(), new ThreadPoolExecutor.DiscardOldestPolicy());
            executor.allowCoreThreadTimeOut(true);
            return executor;
        }
    }
}
