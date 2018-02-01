package cc.colorcat.jspider;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxx on 2017-11-9.
 * xx.ch@outlook.com
 */
final class RealCall implements Call {
    private final JSpider spider;
    private final Seed seed;
    private final Connection connection;
    private final Parser parser;
    private int retryCount = 0;

    RealCall(JSpider spider, Seed seed) {
        this.spider = spider;
        this.seed = seed;
        this.connection = spider.connection();
        this.parser = spider.parserProxy;
    }

    @Override
    public synchronized int retryCount() {
        return retryCount;
    }

    @Override
    public synchronized void incrementRetryCount() {
        ++retryCount;
    }

    @Override
    public Seed seed() {
        return seed;
    }

    @Override
    public void execute() {
        Exception reason = null;
        try {
            List<? extends Seed> newSeeds = getScrapsWithInterceptorChain();
            if (!newSeeds.isEmpty()) {
                spider.mapAndEnqueue(newSeeds);
            }
        } catch (Exception e) {
            reason = e;
        } finally {
            spider.dispatcher().finished(this, reason);
        }
    }

    @Override
    public void run() {
        execute();
    }

    private List<Scrap> getScrapsWithInterceptorChain() throws IOException {
        List<Interceptor> users = spider.interceptors();
        List<Interceptor> interceptors = new ArrayList<>(users.size() + 3);
        interceptors.add(new SeedsCleanerInterceptor(spider));
        interceptors.add(new HandlerInterceptor(spider));
        interceptors.addAll(users);
        interceptors.add(new ConnectionInterceptor());
        Interceptor.Chain chain = new RealInterceptorChain(interceptors, 0, seed, connection, parser);
        return chain.proceed(seed);
    }
}
