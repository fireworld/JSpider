package cc.colorcat.spider;

import com.sun.istack.internal.Nullable;

public interface Parser<T> {

    /**
     * Parse the {@link WebSnapshot} into {@link Scrap}.
     *
     * @return instance of {@link Scrap} if success else null
     */
    @Nullable
    Scrap<? extends T> parse(WebSnapshot snapshot);
}
