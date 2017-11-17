package cc.colorcat.jspider;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
final class Dispatcher {
    private final JSpider spider;
    private final ExecutorService executor;
    private final List<Seed> reachedMaxDepth = new LinkedList<>();
    private final LinkedList<Call> finished = new LinkedList<>();
    private final LinkedList<Call> waiting = new LinkedList<>();
    private final LinkedList<Call> running = new LinkedList<>();

    Dispatcher(JSpider spider, ExecutorService executor) {
        this.spider = spider;
        this.executor = executor;
    }

    synchronized void enqueue(final List<Call> calls, boolean depthFirst) {
        if (depthFirst) {
            for (int i = calls.size() - 1; i >= 0; i--) {
                Call call = calls.get(i);
                if (!uriExists(call.seed().uri())) {
                    waiting.addFirst(call);
                }
            }
        } else {
            for (Call call : calls) {
                if (!uriExists(call.seed().uri())) {
                    waiting.addLast(call);
                }
            }
        }
        promoteCalls();
    }

    private void promoteCalls() {
        if (running.size() >= spider.maxSeedOnRunning()) return;
        if (!waiting.isEmpty()) {
            Iterator<Call> iterator = waiting.iterator();
            while (iterator.hasNext()) {
                Call call = iterator.next();
                running.add(call);
                executor.submit(call);
                iterator.remove();
                if (running.size() >= spider.maxSeedOnRunning()) break;
            }
        } else if (running.isEmpty()) {
            onAllFinished();
        }
    }

    /**
     * @param call   the executed task
     * @param reason the specified {@link Call} executed successful if reason is null, else failed.
     */
    synchronized void finished(final Call call, final Exception reason) {
        Seed seed = call.seed();
        URI uri = seed.uri();
        removeByUri(running, uri);
        if (reason == null) {
            finished.add(call); // successful
            promoteCalls();
        } else {
            call.incrementRetryCount();
            if (call.retryCount() <= spider.maxRetry()) {
                enqueue(Collections.singletonList(call), spider.depthFirst()); // retry
            } else {
                finished.add(call); // failed
                promoteCalls();
            }
        }
        if (reason == null) {
            spider.listener().onSuccess(seed);
        } else {
            spider.listener().onFailure(seed, reason);
        }
    }

    void handled(final Scrap scrap) {
        spider.listener().onHandled(scrap);
    }

    void onReachedMaxDepth(Seed seed) {
        synchronized (reachedMaxDepth) {
            reachedMaxDepth.add(seed);
        }
        spider.listener().onReachedMaxDepth(seed);
    }

    private void onAllFinished() {
        List<Seed> success = new ArrayList<>();
        List<Seed> failed = new ArrayList<>();
        for (Call call : finished) {
            Seed seed = call.seed();
            if (call.retryCount() > spider.maxRetry()) {
                failed.add(seed);
            } else {
                success.add(seed);
            }
        }
        spider.seedJar().save(success, failed, reachedMaxDepth);
    }

    private boolean uriExists(final URI uri) {
        return containsByUri(running, uri) || containsByUri(waiting, uri) || containsByUri(finished, uri);
    }

    private static boolean containsByUri(final Collection<Call> calls, final URI uri) {
        for (Call call : calls) {
            if (uri.equals(call.seed().uri())) {
                return true;
            }
        }
        return false;
    }

    private static void removeByUri(Collection<Call> calls, URI uri) {
        calls.removeIf(call -> uri.equals(call.seed().uri()));
    }
}
