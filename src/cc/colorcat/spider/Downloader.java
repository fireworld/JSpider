package cc.colorcat.spider;

import cc.colorcat.spider.internal.Log;
import cc.colorcat.spider.internal.Utils;
import javafx.scene.Parent;
import okhttp3.*;
import okhttp3.Call;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.io.IOException;

public class Downloader {

    private OkHttpClient client;

    public Downloader(OkHttpClient client) {
        this.client = client;
    }

    public void download(String url, File savePath) throws IOException {
        if (createDir(savePath)) {
            Response response = client.newCall(new Request.Builder().url(url).get().build()).execute();
            write(response, savePath);
        }
    }

    public void submit(String url, File savePath) {
        if (createDir(savePath)) {
            client.newCall(new Request.Builder().url(url).get().build()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.w("Download failure, url = " + call.request().url());
                    Log.e(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    write(response, savePath);
                }
            });
        }
    }

    private static boolean createDir(File savePath) {
        File parent = savePath.getParentFile();
        return parent.exists() || parent.mkdirs();
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
                } finally {
                    Utils.close(bi);
                    Utils.close(bo);
                    Utils.close(body);
                }
            }
        }
    }
}
