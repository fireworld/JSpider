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
    //    private final List<Scrap> handled = new LinkedList<>();
    private final List<Seed> reachedMaxDepth = new LinkedList<>();
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

    synchronized boolean tryEnqueueRunning(Call call) {
        URI uri = call.seed().uri();
        return !contains(uri) && running.put(uri, call) == null;
    }

    // 从 running 中移除，并根据情况添加至 finished 或 waiting
    synchronized void finished(Call call, Exception reason) {
        Seed seed = call.seed();
        URI uri = seed.uri();
        running.remove(uri);
        if (reason == null) {
            finished.put(uri, call);
            promoteCalls();
        } else if (call.count() < spider.maxTry()) {
            enqueue(call);
        } else {
            call.incrementCount();
            finished.put(uri, call);
            promoteCalls();
        }
        System.out.println("running size = " + running.size());
        System.out.println("waiting size = " + waiting.size());
//        if (success || call.count() >= spider.maxTry()) {
//            finished.put(uri, call);
//            promoteCalls();
//        } else {
//            enqueue(call);
//        }
        EventListener listener = spider.listener();
        if (reason == null) {
            listener.onSuccess(seed);
        } else {
            listener.onFailure(seed, reason);
        }
    }

    synchronized void handled(Scrap scrap) {
        URI uri = scrap.uri();
        assert waiting.remove(uri) == null; // todo 这儿是不需要的，暂时加在这儿测试。
//        handled.add(scrap);
        spider.listener().onHandled(scrap);
    }

    synchronized void onReachedMaxDepth(Seed seed) {
        reachedMaxDepth.add(seed);
        spider.listener().onReachedMaxDepth(seed);
    }

    private void onAllFinished() {
        Collection<Call> calls = finished.values();
        List<Seed> success = new ArrayList<>();
        List<Seed> failed = new ArrayList<>();
        for (Call call : calls) {
            Seed seed = call.seed();
            if (call.count() > spider.maxTry()) {
                failed.add(seed);
            } else {
                success.add(seed);
            }
        }
//        spider.listener().onFinished(all, failed, handled);
        spider.seedJar().save(success, failed, reachedMaxDepth);
    }

    private boolean contains(URI uri) {
        return running.containsKey(uri) || waiting.containsKey(uri) || finished.containsKey(uri);
    }
}
