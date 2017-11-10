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

    synchronized void enqueue(List<Call> calls) {
        for (Call call : calls) {
            URI uri = call.seed().uri();
            if (!contains(uri)) {
                waiting.put(uri, call);
            }
        }
        promoteCalls();
    }

    private void enqueue(Call call) {
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
            onAllFinished();
        }
    }

    synchronized boolean tryEnqueueRunning(RealCall realCall) {
        URI uri = realCall.seed().uri();
        return !contains(uri) && running.put(uri, realCall) == null;
    }

    // 从 running 中移除，并根据情况添加至 finished 或 waiting
    synchronized void finished(RealCall call, boolean success) {
        Scrap seed = call.seed();
        URI uri = seed.uri();
        running.remove(uri);
        if (success) {
            finished.put(uri, call);
            promoteCalls();
        } else if (call.count() < spider.maxTry()) {
            enqueue(call);
        } else {
            call.incrementCount();
            finished.put(uri, call);
            promoteCalls();
        }
        Log.d("running size = " + running.size());
        Log.w("waiting size = " + waiting.size());
//        if (success || call.count() >= spider.maxTry()) {
//            finished.put(uri, call);
//            promoteCalls();
//        } else {
//            enqueue(call);
//        }
        EventListener listener = spider.listener();
        if (success) {
            listener.onSuccess(seed);
        } else {
            listener.onFailed(seed);
        }
    }

    synchronized void handled(Scrap scrap) {
        URI uri = scrap.uri();
        waiting.remove(uri);
        handled.add(scrap);
        spider.listener().onHandled(scrap);
    }

    private boolean contains(URI uri) {
        return running.containsKey(uri) || waiting.containsKey(uri) || finished.containsKey(uri);
    }

    private void onAllFinished() {
        Collection<Call> scraps = finished.values();
        List<Scrap> all = new ArrayList<>(scraps.size());
        List<Scrap> failed = new ArrayList<>();
        for (Call call : scraps) {
            Scrap scrap = call.seed();
            all.add(scrap);
            if (call.count() > spider.maxTry()) {
                failed.add(scrap);
            }
        }
        spider.listener().onFinished(all, failed, handled);
    }
}
