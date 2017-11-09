package cc.colorcat.spider;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;

final class Dispatcher {
    private JSpider spider;
    private LinkedHashMap<URI, Call> finished = new LinkedHashMap<>();
    private LinkedHashMap<URI, Call> waiting = new LinkedHashMap<>();
    private LinkedHashMap<URI, Call> running = new LinkedHashMap<>();

    synchronized void enqueue(Scrap scrap) {

    }

    synchronized void enqueue(List<Scrap> scraps) {

    }

    synchronized void enqueue(RealCall call) {

    }

    synchronized boolean contains(URI uri) {
        return false;
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
        } else {
            enqueue(call);
        }
    }

    synchronized void handled(Scrap scrap) {
        finished.put(scrap.uri(), new EmptyCall(scrap));
    }
}
