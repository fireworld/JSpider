package cc.colorcat.jspider;

import java.util.List;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public interface Parser {

    /**
     * Parse the {@link WebSnapshot} into {@link Scrap}.
     *
     * @return {@link List<Scrap>} if success else empty list.
     */
    List<Scrap> parse(Seed seed, WebSnapshot snapshot);
}
