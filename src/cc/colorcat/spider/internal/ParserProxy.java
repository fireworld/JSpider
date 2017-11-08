package cc.colorcat.spider.internal;

import cc.colorcat.spider.Parser;
import cc.colorcat.spider.Scrap;
import cc.colorcat.spider.WebSnapshot;

import java.util.LinkedList;
import java.util.List;

public class ParserProxy<T> implements Parser<T> {
    private List<Parser<T>> parsers = new LinkedList<>();

    public void registerParser(Parser<T> parser) {
        if (parser != null && !this.parsers.contains(parser)) {
            this.parsers.add(parser);
        }
    }

    public void registerParsers(List<Parser<? extends T>> parsers) {
        if (parsers != null) {

        }
    }

//    @Override
//    public List<Scrap<? extends T>> parse(Scrap<? super T> scrap, WebSnapshot snapshot) {
//        return null;
//    }

    @Override
    public List<Scrap<? extends T>> parse(Scrap<T> scrap, WebSnapshot snapshot) {
        List<Scrap<? extends T>> result = null;
        for (Parser<T> parser : this.parsers) {
            result = parser.parse(scrap, snapshot);
            if (result != null) {
                break;
            }
        }
        return result;
    }
}
