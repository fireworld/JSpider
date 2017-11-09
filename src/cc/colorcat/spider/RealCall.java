package cc.colorcat.spider;

import cc.colorcat.spider.internal.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class RealCall implements Call {
    private final JSpider spider;
    private final Scrap seed;
    private final Connection connection;
    private int count = 0;

    RealCall(JSpider spider, Scrap seed) {
        this.spider = spider;
        this.seed = seed;
        this.connection = spider.connection().clone();
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public Scrap seed() {
        return seed;
    }

    @Override
    public void execute() throws IOException {
        boolean success = false;
        try {
            List<Scrap> newScraps = getScrapsWitInterceptorChain();
            success = true; // reach her is success
            List<Scrap> unhandled = tryHandle(newScraps);
            if (!unhandled.isEmpty()) {
                if (spider.depthFirst()) {
                    depthCrawl(unhandled, false);
                } else {
                    breadthCrawl(unhandled);
                }
            }
        } finally {
            spider.dispatcher().finished(this, success);
        }
    }

    @Override
    public void run() {
        synchronized (seed) {
            boolean success = false;
            try {
                List<Scrap> newScraps = getScrapsWitInterceptorChain();
                success = true; // reach her is success
                List<Scrap> unhandled = tryHandle(newScraps);
                if (!unhandled.isEmpty()) {
                    if (spider.depthFirst()) {
                        depthCrawl(unhandled, true);
                    } else {
                        breadthCrawl(unhandled);
                    }
                }
            } catch (IOException e) {
                Log.e(e);
            } finally {
                spider.dispatcher().finished(this, success);
            }
        }
    }

    private List<Scrap> tryHandle(List<Scrap> scraps) {
        Iterator<Scrap> iterable = scraps.iterator();
        while (iterable.hasNext()) {
            Scrap scrap = iterable.next();
            if (scrap.data().isEmpty()) continue;
            boolean handled = false;
            List<Handler> handlers = spider.handlers(scrap.tag());
            for (Handler handler : handlers) {
                if (handler.handle(scrap)) {
                    handled = true;
                }
            }
            if (handled) {
                iterable.remove();
                spider.dispatcher().handled(scrap);
            }
        }
        return scraps;
    }

    private void depthCrawl(List<Scrap> scraps, boolean onRun) throws IOException {
        LinkedList<Scrap> newScraps = new LinkedList<>(scraps);
        for (Scrap scrap = newScraps.pollFirst(); scrap != null; scrap = newScraps.pollFirst()) {
            RealCall call = new RealCall(spider, scrap);
            if (spider.dispatcher().tryEnqueueRunning(call)) {
                if (onRun) {
                    call.run();
                } else {
                    call.execute();
                }
            }
        }
    }

    private void breadthCrawl(List<Scrap> scraps) {
        spider.dispatcher().enqueue(scraps);
    }

    private List<Scrap> getScrapsWitInterceptorChain() throws IOException {
        count++;
        List<Interceptor> users = spider.interceptors();
        List<Interceptor> interceptors = new ArrayList<>(users.size() + 1);
        interceptors.addAll(users);
        interceptors.add(new ParserInterceptor(spider.parsers()));
        Interceptor.Chain chain = new RealInterceptorChain(interceptors, 0, seed, connection);
        return chain.proceed(seed);
    }
}
