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
    private final Scrap seed;
    private final Connection connection;

    public RealInterceptorChain(List<Interceptor> interceptors, int index, Scrap seed, Connection connection) {
        this.interceptors = interceptors;
        this.index = index;
        this.seed = seed;
        this.connection = connection;
    }

    @Override
    public Connection connection() {
        return connection;
    }

    @Override
    public Scrap seed() {
        return seed;
    }

    @Override
    public List<Scrap> proceed(Scrap seed) throws IOException {
        RealInterceptorChain next = new RealInterceptorChain(interceptors, index + 1, seed, connection);
        Interceptor interceptor = interceptors.get(index);
        return interceptor.intercept(next);
    }
}
