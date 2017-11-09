package cc.colorcat.spider;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

final class Dispatcher {
    private ExecutorService service;
    private JSpider spider;
    private LinkedHashMap<URI, Call> finished = new LinkedHashMap<>();
    private LinkedHashMap<URI, Call> waiting = new LinkedHashMap<>();
    private LinkedHashMap<URI, Call> running = new LinkedHashMap<>();

    synchronized void enqueue(Scrap scrap) {
    }

    synchronized void enqueue(List<Scrap> scraps) {

    }

    private synchronized void enqueue(RealCall call) {
        URI uri = call.seed().uri();
        if (!contains(uri)) {
            waiting.put(uri, call);
            promoteCalls();
        }
    }

    private synchronized void promoteCalls() {
        if (running.size() >= spider.maxSeedOnRunning()) return;
        if (!waiting.isEmpty()) {
            Iterator<Call> values = waiting.values().iterator();
            while (values.hasNext()) {
                Call call = values.next();
                running.put(call.seed().uri(), call);
                service.submit(call);
                values.remove();
                if (running.size() >= spider.maxSeedOnRunning()) break;
            }
        }
    }

    synchronized boolean tryEnqueueRunning(RealCall realCall) {
        URI uri = realCall.seed().uri();
        return !contains(uri) && running.put(uri, realCall) == null;
    }

    // 从 running 中移除，并根据情况添加至 finished 或 waiting
    synchronized void finished(RealCall call, boolean success) {
        URI uri = call.seed().uri();
        running.remove(uri);
        if (success || call.count() >= spider.maxRetry()) {
            finished.put(uri, call);
            promoteCalls();
        } else {
            enqueue(call);
        }
    }

    synchronized void handled(Scrap scrap) {
        URI uri = scrap.uri();
        waiting.remove(uri);
        finished.put(uri, new EmptyCall(scrap));
    }

    private synchronized boolean contains(URI uri) {
        return !running.containsKey(uri) && !waiting.containsKey(uri) && !finished.containsKey(uri);
    }
}
