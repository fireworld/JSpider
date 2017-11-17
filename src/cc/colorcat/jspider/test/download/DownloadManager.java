package cc.colorcat.jspider.test.download;

import cc.colorcat.jspider.internal.Log;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by cxx on 2017-11-15.
 * xx.ch@outlook.com
 */
public class DownloadManager {
    private final Map<URI, Task> finished = new LinkedHashMap<>();
    private final Map<URI, Task> running = new LinkedHashMap<>();
    private Downloader downloader;
    private int maxRetry;
    private Listener listener;

    public static DownloadManager create(Downloader downloader) {
        return create(downloader, 3);
    }

    public static DownloadManager create(Downloader downloader, int maxRetry) {
        Objects.requireNonNull(downloader, "downloader == null");
        if (maxRetry < 0) throw new IllegalArgumentException("maxRetry < 0");
        DownloadManager manager = new DownloadManager(downloader, maxRetry);
        manager.listener = new RetryListener(manager, null);
        return manager;
    }

    public static DownloadManager create(Downloader downloader, int maxRetry, Listener listener) {
        Objects.requireNonNull(downloader, "downloader == null");
        Objects.requireNonNull(listener, "listener == null");
        if (maxRetry < 0) throw new IllegalArgumentException("maxRetry < 0");
        DownloadManager manager = new DownloadManager(downloader, maxRetry);
        manager.listener = new RetryListener(manager, listener);
        return manager;
    }

    private DownloadManager(Downloader downloader, int maxRetry) {
        this.downloader = downloader;
        this.maxRetry = maxRetry;
    }

    public void enqueue(Task task) {
        if (!this.running.containsKey(task.uri())) {
            realEnqueue(task);
        }
    }

    private void realEnqueue(Task task) {
        downloader.clone().task(task).go(listener);
    }

    private void notifyRequestRemoved() {
        if (running.isEmpty()) {
            Collection<Task> all = finished.values();
            List<Task> success = all.stream().filter(request -> request.retryCount() <= maxRetry).collect(Collectors.toList());
            List<Task> failure = all.stream().filter(request -> request.retryCount() > maxRetry).collect(Collectors.toList());
            listener.onAllFinished(success, failure);
        }
    }

    private static class RetryListener implements Listener {
        private DownloadManager manager;
        private Listener listener;

        private RetryListener(DownloadManager manager, Listener listener) {
            this.manager = manager;
            this.listener = listener;
        }

        @Override
        public synchronized void onSuccess(Task task) {
            manager.running.remove(task.uri());
            manager.finished.put(task.uri(), task);
            manager.notifyRequestRemoved();
            Log.f("Download success, uri = " + task.uri());
            if (listener != null) {
                listener.onSuccess(task);
            }
        }

        @Override
        public synchronized void onFailure(Task task, Exception reason) {
            task.incrementRetryCount();
            if (task.retryCount() <= manager.maxRetry) {
                manager.realEnqueue(task);
                Log.w("Download failed and will retry, uri = " + task.uri() + ", retryCount = " + task.retryCount());
            } else {
                manager.running.remove(task.uri());
                manager.finished.put(task.uri(), task);
                manager.notifyRequestRemoved();
                Log.s("Download failed, uri = " + task.uri() + "\n\t\treason = " + reason.toString());
            }
            if (listener != null) {
                listener.onFailure(task, reason);
            }
        }

        @Override
        public void onAllFinished(List<Task> success, List<Task> failure) {
            Log.i("All download finished!");
            if (listener != null) {
                listener.onAllFinished(success, failure);
            }
        }
    }

    public interface Listener extends Downloader.Callback {

        void onAllFinished(List<Task> success, List<Task> failure);
    }
}
