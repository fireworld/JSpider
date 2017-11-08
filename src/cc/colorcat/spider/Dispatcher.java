package cc.colorcat.spider;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;

public final class Dispatcher {
    private ExecutorService executor;
    private final ConcurrentSkipListSet<Scrap<?>> executed = new ConcurrentSkipListSet<>(Comparator.comparing(Scrap::uri));
    private final ConcurrentSkipListSet<Scrap<?>> waiting = new ConcurrentSkipListSet<>(Comparator.comparing(Scrap::uri));
}
