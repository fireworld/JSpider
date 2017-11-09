//package cc.colorcat.spider;
//
//import cc.colorcat.spider.internal.Log;
//
//import java.io.IOException;
//import java.util.Comparator;
//import java.util.List;
//import java.util.concurrent.ConcurrentSkipListSet;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public final class Dispatcher {
//    private JSpider spider;
//    private ExecutorService executor;
//    private final ConcurrentSkipListSet<Scrap<?>> success = new ConcurrentSkipListSet<>(Comparator.comparing(Scrap::uri));
//    private final ConcurrentSkipListSet<Scrap<?>> parseFailed = new ConcurrentSkipListSet<>(Comparator.comparing(Scrap::uri));
//
//    private final ConcurrentSkipListSet<Scrap<?>> connectFailed = new ConcurrentSkipListSet<>(Comparator.comparing(Scrap::uri));
//    private final ConcurrentSkipListSet<Scrap<?>> waiting = new ConcurrentSkipListSet<>(Comparator.comparing(Scrap::uri));
//    private final ConcurrentSkipListSet<Scrap<?>> running = new ConcurrentSkipListSet<>(Comparator.comparing(Scrap::uri));
//    private AtomicInteger tryCount = new AtomicInteger(0);
//
//    Dispatcher(JSpider spider, ExecutorService executor) {
//        this.spider = spider;
//        this.executor = executor;
//    }
//
//    synchronized void start(List<Scrap<?>> seeds) {
//        success.clear();
//        connectFailed.clear();
//        parseFailed.clear();
//        running.clear();
//        waiting.clear();
//        tryCount.set(0);
//        waiting.addAll(seeds);
//        promoteTask();
//    }
//
//    private synchronized void retry() {
//        if (tryCount.incrementAndGet() < spider.maxTry()) {
//            if (!connectFailed.isEmpty()) {
//                Log.i("------------------------ retry start ------------------------");
//                waiting.addAll(connectFailed);
//                connectFailed.clear();
//                promoteTask();
//            } else {
//                if (parseFailed.isEmpty()) {
//                    Log.i("all task success");
//                    success.clear();
//                } else {
//                    Log.i("all task finished");
//                    spider.printParseFailed(parseFailed);
//                    success.clear();
//                    parseFailed.clear();
//                }
//            }
//        } else {
//            Log.i("all task finished, there are " + (connectFailed.size() + parseFailed.size()) + " still failed");
//            spider.printParseFailed(parseFailed);
//            spider.printConnectFailed(connectFailed);
//        }
//    }
//
//    private synchronized void promoteTask() {
//        if (!waiting.isEmpty()) {
//            for (Scrap<?> scrap = waiting.pollFirst(); scrap != null; scrap = waiting.pollFirst()) {
//                if (!success.contains(scrap) && !parseFailed.contains(scrap) && !connectFailed.contains(scrap)) {
//                    if (running.add(scrap)) {
//                        executor.execute(new AsyncTask(scrap));
//                    }
//                }
//            }
//        } else if (running.isEmpty()) {
//            retry();
//        }
//    }
//
//
//    private void onSuccess(Scrap<?> scrap) {
//        success.add(scrap);
//        Log.f("fetch " + scrap.uri().toString() + " success");
//    }
//
//    private void onParseFailed(Scrap<?> scrap) {
//        parseFailed.add(scrap);
//        Log.w("there is no parser to handle uri = " + scrap.uri().toString());
//    }
//
//    private void onGetWebSnapshotFailed(Scrap<?> scrap) {
//        connectFailed.add(scrap);
//        Log.w("connect to " + scrap.uri().toString() + " failed, will retry a later");
//    }
//
//    private void onNewScrap(Scrap<?> newScrap) {
//        waiting.add(newScrap);
//    }
//
//    private void onFinish(Scrap<?> scrap) {
//        running.remove(scrap);
//        promoteTask();
//    }
//
//    private class AsyncTask implements Runnable {
//        private final Scrap<?> scrap;
//
//        private AsyncTask(Scrap<?> scrap) {
//            this.scrap = scrap;
//        }
//
//        @Override
//        public void run() {
//            try {
//                JSpider spider = Dispatcher.this.spider;
//                WebSnapshot snapshot = spider.connector().clone().get(spider, scrap.uri());
//                if (snapshot != null && snapshot.isSuccess()) {
//                    List<Scrap> newScraps = (List) scrap.tryParse(snapshot);
//                    if (newScraps == null || newScraps.isEmpty()) {
//                        Dispatcher.this.onParseFailed(scrap);
//                    } else {
//                        for (Scrap newScrap : newScraps) {
//                            if (newScrap.isTarget()) {
//                                newScrap.deliver();
//                                Dispatcher.this.onSuccess(newScrap);
//                            } else {
//                                Dispatcher.this.onNewScrap(newScrap);
//                            }
//                        }
//                    }
//                } else {
//                    Dispatcher.this.onGetWebSnapshotFailed(scrap);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                Dispatcher.this.onGetWebSnapshotFailed(scrap);
//            } finally {
//                Dispatcher.this.onFinish(scrap);
//            }
//        }
//    }
//}
