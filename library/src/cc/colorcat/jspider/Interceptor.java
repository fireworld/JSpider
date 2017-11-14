package cc.colorcat.jspider;

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

        Parser parser();

        Seed seed();

        List<Scrap> proceed(Seed seed) throws IOException;
    }
}
