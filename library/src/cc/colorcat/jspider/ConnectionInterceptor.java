package cc.colorcat.jspider;

import java.io.IOException;
import java.util.List;

/**
 * Created by cxx on 2017-11-9.
 * xx.ch@outlook.com
 */
class ConnectionInterceptor implements Interceptor {

    ConnectionInterceptor() {
    }

    @Override
    public List<Scrap> intercept(Chain chain) throws IOException {
        Seed seed = chain.seed();
        WebSnapshot snapshot = chain.connection().get(seed.uri());
        return chain.parser().parse(seed, snapshot);
    }
}
