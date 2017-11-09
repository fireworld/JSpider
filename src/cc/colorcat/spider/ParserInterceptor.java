package cc.colorcat.spider;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class ParserInterceptor implements Interceptor {
    private List<Parser> parsers;

    ParserInterceptor(List<Parser> parsers) {
        this.parsers = parsers;
    }

    @Override
    public List<Scrap> intercept(Chain chain) throws IOException {
        List<Scrap> result = new LinkedList<>();
        Scrap seed = chain.seed();
        WebSnapshot snapshot = chain.connection().get(seed.uri());
        if (snapshot.isSuccess()) {
            for (Parser parser : parsers) {
                result.addAll(parser.parse(seed, snapshot));
            }
        }
        return result;
    }
}
