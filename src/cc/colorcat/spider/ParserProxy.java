package cc.colorcat.spider;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxx on ${DATA}.
 * xx.ch@outlook.com
 */
final class ParserProxy implements Parser {
    private List<Parser> parsers;

    ParserProxy(List<Parser> parsers) {
        this.parsers = parsers;
    }

    @Override
    public List<Scrap> parse(Scrap seed, WebSnapshot snapshot) {
        List<Scrap> result = new LinkedList<>();
        if (snapshot != null && snapshot.isSuccess()) {
            for (int i = 0, size = parsers.size(); i < size; ++i) {
                result.addAll(parsers.get(i).parse(seed, snapshot));
            }
        }
        return result;
    }
}
