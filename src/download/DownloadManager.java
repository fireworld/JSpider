package download;

import cc.colorcat.jspider.internal.Log;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by cxx on 17-11-15.
 * xx.ch@outlook.com
 */
public class DownloadManager {
    private final Map<String, Request> finished = new LinkedHashMap<>();
    private final Map<String, Request> requests = new LinkedHashMap<>();
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
        manager.listener = new RetryCallback(manager, null);
        return manager;
    }

    public static DownloadManager create(Downloader downloader, int maxRetry, Listener listener) {
        Objects.requireNonNull(downloader, "downloader == null");
        Objects.requireNonNull(listener, "listener == null");
        if (maxRetry < 0) throw new IllegalArgumentException("maxRetry < 0");
        DownloadManager manager = new DownloadManager(downloader, maxRetry);
        manager.listener = new RetryCallback(manager, listener);
        return manager;
    }

    private DownloadManager(Downloader downloader, int maxRetry) {
        this.downloader = downloader;
        this.maxRetry = maxRetry;
    }

    public void enqueue(Request request) {
        if (!this.requests.containsKey(request.url())) {
            realEnqueue(request);
        }
    }

    private void realEnqueue(Request request) {
        downloader.clone().request(request).go(listener);
    }

    private void notifyRequestRemoved() {
        if (requests.isEmpty()) {
            Collection<Request> all = finished.values();
            List<Request> success = all.stream().filter(request -> request.retryCount() <= maxRetry).collect(Collectors.toList());
            List<Request> failure = all.stream().filter(request -> request.retryCount() > maxRetry).collect(Collectors.toList());
            listener.onAllFinished(success, failure);
        }
    }

    private static class RetryCallback implements Listener {
        private DownloadManager manager;
        private Listener listener;

        private RetryCallback(DownloadManager manager, Listener listener) {
            this.manager = manager;
            this.listener = listener;
        }

        @Override
        public synchronized void onSuccess(Request request) {
            manager.requests.remove(request.url());
            manager.finished.put(request.url(), request);
            manager.notifyRequestRemoved();
            Log.f("Download success, url = " + request.url());
            if (listener != null) {
                listener.onSuccess(request);
            }
        }

        @Override
        public synchronized void onFailure(Request request, Exception reason) {
            request.incrementRetryCount();
            if (request.retryCount() <= manager.maxRetry) {
                manager.realEnqueue(request);
                Log.w("Download failed and will retry, url = " + request.url() + ", retryCount = " + request.retryCount());
            } else {
                manager.requests.remove(request.url());
                manager.finished.put(request.url(), request);
                manager.notifyRequestRemoved();
                Log.s("Download failed, url = " + request.url() + "\n\t\treason = " + reason.toString());
            }
            if (listener != null) {
                listener.onFailure(request, reason);
            }
        }

        @Override
        public void onAllFinished(List<Request> success, List<Request> failure) {
            if (listener != null) {
                listener.onAllFinished(success, failure);
            }
            Log.i("All download finished!");
        }
    }

    public interface Listener extends Downloader.Callback {

        void onAllFinished(List<Request> success, List<Request> failure);
    }
}
