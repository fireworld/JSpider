package cc.colorcat.spider;

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
    private final LinkedList<Call> finished = new LinkedList<>();
    private final LinkedList<Call> waiting = new LinkedList<>();
    private final LinkedList<Call> running = new LinkedList<>();

    Dispatcher(JSpider spider, ExecutorService executor) {
        this.spider = spider;
        this.executor = executor;
    }

    synchronized void enqueue(List<Call> calls, boolean depthFirst) {
        if (depthFirst) {
            for (int i = calls.size() - 1; i >= 0; i--) {
                Call call = calls.get(i);
                if (!contains(call.seed().uri())) {
                    waiting.addFirst(call);
                }
            }
        } else {
            for (Call call : calls) {
                if (!contains(call.seed().uri())) {
                    waiting.addLast(call);
                }
            }
        }
        promoteCalls();
    }

    private void promoteCalls() {
        if (running.size() >= spider.maxSeedOnRunning()) return;
        if (!waiting.isEmpty()) {
            Iterator<Call> values = waiting.iterator();
            while (values.hasNext()) {
                Call call = values.next();
                running.add(call);
                executor.submit(call);
                values.remove();
                if (running.size() >= spider.maxSeedOnRunning()) break;
            }
        } else if (running.isEmpty()) {
            onAllFinished();
        }
    }

    // 从 running 中移除，并根据情况添加至 finished 或 waiting
    synchronized void finished(Call call, Exception reason) {
        Seed seed = call.seed();
        URI uri = seed.uri();
        removeByURI(running, uri);
        if (reason == null) {
            finished.add(call);
            promoteCalls();
        } else if (call.count() < spider.maxTry()) {
            enqueue(Collections.singletonList(call), spider.depthFirst());
        } else {
            call.incrementCount();
            finished.add(call);
            promoteCalls();
        }
        System.out.println("running size = " + running.size());
        System.out.println("waiting size = " + waiting.size());
        EventListener listener = spider.listener();
        if (reason == null) {
            listener.onSuccess(seed);
        } else {
            listener.onFailure(seed, reason);
        }
    }

    synchronized void handled(Scrap scrap) {
//        URI uri = scrap.uri();
//        assert !removeByURI(waiting, uri); // todo 这儿是不需要的，暂时加在这儿测试。
//        handled.add(scrap);
        spider.listener().onHandled(scrap);
    }

    synchronized void onReachedMaxDepth(Seed seed) {
        reachedMaxDepth.add(seed);
        spider.listener().onReachedMaxDepth(seed);
    }

    private void onAllFinished() {
        List<Seed> success = new ArrayList<>();
        List<Seed> failed = new ArrayList<>();
        for (Call call : finished) {
            Seed seed = call.seed();
            if (call.count() > spider.maxTry()) {
                failed.add(seed);
            } else {
                success.add(seed);
            }
        }
        spider.seedJar().save(success, failed, reachedMaxDepth);
    }

    private boolean contains(URI uri) {
        return containsByURI(running, uri) || containsByURI(waiting, uri) || containsByURI(finished, uri);
    }

    private static boolean containsByURI(Collection<Call> calls, final URI uri) {
        for (Call call : calls) {
            if (uri.equals(call.seed().uri())) {
                return true;
            }
        }
        return false;
    }

    private static void removeByURI(Collection<Call> calls, final URI uri) {
        calls.removeIf(call -> uri.equals(call.seed().uri()));
    }
}
