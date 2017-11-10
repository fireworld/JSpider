package cc.colorcat.spider;

import cc.colorcat.spider.internal.Log;

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
    private final List<Scrap> handled = new LinkedList<>();
    private final LinkedHashMap<URI, Call> finished = new LinkedHashMap<>();
    private final LinkedHashMap<URI, Call> waiting = new LinkedHashMap<>();
    private final LinkedHashMap<URI, Call> running = new LinkedHashMap<>();

    Dispatcher(JSpider spider, ExecutorService executor) {
        this.spider = spider;
        this.executor = executor;
    }

    synchronized void enqueue(Scrap scrap) {
        enqueue(new RealCall(spider, scrap));
    }

    synchronized void enqueue(List<Scrap> scraps) {
        for (Scrap scrap : scraps) {
            enqueue(new RealCall(spider, scrap));
        }
    }

    private void enqueue(RealCall call) {
        URI uri = call.seed().uri();
        if (!contains(uri)) {
            waiting.put(uri, call);
            promoteCalls();
        }
    }

    private void promoteCalls() {
        if (running.size() >= spider.maxSeedOnRunning()) return;
        if (!waiting.isEmpty()) {
            Iterator<Call> values = waiting.values().iterator();
            while (values.hasNext()) {
                Call call = values.next();
                running.put(call.seed().uri(), call);
                executor.submit(call);
                values.remove();
                if (running.size() >= spider.maxSeedOnRunning()) break;
            }
        } else if (running.isEmpty()) {
            logAllFailed();
        }
    }

    synchronized boolean tryEnqueueRunning(RealCall realCall) {
        URI uri = realCall.seed().uri();
        return !contains(uri) && running.put(uri, realCall) == null;
    }

    // 从 running 中移除，并根据情况添加至 finished 或 waiting
    synchronized void finished(RealCall call, boolean success) {
        Scrap scrap = call.seed();
        URI uri = scrap.uri();
        running.remove(uri);
        if (success || call.count() >= spider.maxRetry()) {
            finished.put(uri, call);
            promoteCalls();
        } else {
            enqueue(call);
        }
        EventListener listener = spider.listener();
        if (success) {
            listener.onCrawlSuccess(scrap);
        } else {
            listener.onCrawlFailed(scrap);
        }
    }

    synchronized void handled(Scrap scrap) {
        URI uri = scrap.uri();
        waiting.remove(uri);
        handled.add(scrap);
        spider.listener().onCrawledData(scrap);
    }

    private synchronized boolean contains(URI uri) {
        return running.containsKey(uri) || waiting.containsKey(uri) || finished.containsKey(uri);
    }

    private void logAllFailed() {
        synchronized (finished) {
            Collection<Call> scraps = finished.values();
            List<Scrap> all = new ArrayList<>(scraps.size());
            List<Scrap> failed = new ArrayList<>();
            for (Call call : scraps) {
                Scrap scrap = call.seed();
                all.add(scrap);
                if (call.count() >= spider.maxRetry()) {
                    failed.add(scrap);
                }
            }
            spider.listener().onCrawlFinished(all, failed);
        }
    }
}
