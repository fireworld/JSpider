package cc.colorcat.spider;

import java.io.IOException;
import java.util.List;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public interface Interceptor {

    List<Scrap> intercept(Chain chain) throws IOException;

    interface Chain {
        Connection connection();

        Scrap seed();

        List<Scrap> proceed(Scrap seed) throws IOException;
    }
}