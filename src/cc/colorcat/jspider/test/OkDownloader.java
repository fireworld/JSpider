package cc.colorcat.jspider.test;

import cc.colorcat.jspider.internal.UserAgent;
import cc.colorcat.jspider.test.download.Downloader;
import cc.colorcat.jspider.test.download.Task;
import okhttp3.*;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by cxx on 2017/11/12.
 * xx.ch@outlook.com
 */
public class OkDownloader implements Downloader {
    private OkHttpClient client;
    private Task task;

    public OkDownloader(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public OkDownloader task(Task task) {
        this.task = task;
        return this;
    }

    @Override
    public void go(final Callback callback) {
        Request request = new okhttp3.Request.Builder()
                .url(task.uri().toString())
                .headers(of(task.headers()))
                .header(UserAgent.NAME, UserAgent.Value.CHROME_MAC)
                .get()
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(task, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    BufferedSink sink = Okio.buffer(Okio.sink(task.savePath()));
                    BufferedSource source = Okio.buffer(Okio.source(response.body().byteStream()));
                    sink.writeAll(source);
                    sink.flush();
                    callback.onSuccess(task);
                } catch (Exception e) {
                    callback.onFailure(task, e);
                }
            }
        });
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public OkDownloader clone() {
        return new OkDownloader(client);
    }

    private static Headers of(Map<String, List<String>> headers) {
        Headers.Builder builder = new Headers.Builder();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                builder.add(key, value);
            }
        }
        return builder.build();
    }
}
