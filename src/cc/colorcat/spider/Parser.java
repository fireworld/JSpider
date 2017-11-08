package cc.colorcat.spider;

import com.sun.istack.internal.Nullable;

import java.util.List;

public interface Parser<T> {

    /**
     * Parse the {@link WebSnapshot} into {@link Scrap}.
     *
     * @return instance of {@link Scrap} if success else null
     */
    @Nullable
    List<Scrap<? extends T>> parse(Scrap<T> scrap, WebSnapshot snapshot);
}
