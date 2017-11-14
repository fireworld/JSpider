import cc.colorcat.spider.internal.Log;
import cc.colorcat.spider.internal.Utils;
import okhttp3.*;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public class Downloader {
    private static final int MAX_RETRY = 3;
    private final Map<String, Task> tasks = new HashMap<>();
    private final OkHttpClient client;

    public Downloader(OkHttpClient client) {
        this.client = client;
    }

    public void download(String url, File savePath) {
        synchronized (tasks) {
            Task task = new Task(url, savePath);
            if (!tasks.containsKey(url)) {
                tasks.put(url, task);
                realDownload(new Task(url, savePath));
            }
        }
    }

    private void realDownload(Task task) {
        client.newCall(new Request.Builder().url(task.url).get().build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(e);
                notifyTaskFinish(task, false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    write(response, task.savePath);
                    notifyTaskFinish(task, true);
                } catch (IOException e) {
                    notifyTaskFinish(task, false);
                    throw e;
                }
            }
        });
    }

    private synchronized void notifyTaskFinish(Task task, boolean success) {
        String msg;
        if (!success) {
            msg = "Download failed, url = ";
            task.count++;
            if (task.count < MAX_RETRY) {
                realDownload(task);
            }
        } else {
            msg = "Download success, url = ";
        }
        Log.w(msg + task.url);
    }

    private void write(Response response, File savePath) throws IOException {
        if (response.code() == 200) {
            ResponseBody body = response.body();
            if (body != null) {
                BufferedSource bi = null;
                BufferedSink bo = null;
                try {
                    bi = Okio.buffer(body.source());
                    bo = Okio.buffer(Okio.sink(savePath));
                    bo.writeAll(bi);
                    bo.flush();
                } finally {
                    Utils.close(bi);
                    Utils.close(bo);
                    Utils.close(body);
                }
            }
        }
    }

    private static class Task {
        private String url;
        private File savePath;
        private int count = 0;

        private Task(String url, File savePath) {
            this.url = url;
            this.savePath = savePath;
        }
    }
}
