package cc.colorcat.spider;

import java.io.IOException;
import java.util.List;

public interface Interceptor {

    Scrap intercept(Chain chain) throws IOException;

    interface Chain {
        Connection connection();

        Scrap seed();

        List<Scrap> proceed(Scrap seed) throws IOException;
    }
}
