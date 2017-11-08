package cc.colorcat.spider;

import java.util.List;

public interface Parser {

    /**
     * Parse the {@link WebSnapshot} into {@link Scrap}.
     *
     * @return {@link List<Scrap>} if success else empty list.
     */
    List<Scrap> parse(Scrap seed, WebSnapshot snapshot);
}
