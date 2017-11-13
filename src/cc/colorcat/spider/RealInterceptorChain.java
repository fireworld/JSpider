package cc.colorcat.spider;

import java.io.IOException;
import java.util.List;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
final class RealInterceptorChain implements Interceptor.Chain {
    private final List<Interceptor> interceptors;
    private final int index;
    private final Seed seed;
    private final Connection connection;
    private final Parser parser;

    RealInterceptorChain(List<Interceptor> interceptors, int index, Seed seed, Connection connection, Parser parser) {
        this.interceptors = interceptors;
        this.index = index;
        this.seed = seed;
        this.connection = connection;
        this.parser = parser;
    }

    @Override
    public Connection connection() {
        return connection;
    }

    @Override
    public Parser parser() {
        return parser;
    }

    @Override
    public Seed seed() {
        return seed;
    }

    @Override
    public List<Scrap> proceed(Seed seed) throws IOException {
        RealInterceptorChain next = new RealInterceptorChain(interceptors, index + 1, seed, connection, parser);
        Interceptor interceptor = interceptors.get(index);
        return interceptor.intercept(next);
    }
}
