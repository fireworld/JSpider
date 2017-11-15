import download.Downloader;
import download.FileUtils;
import download.Request;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by cxx on ${DATA}.
 * xx.ch@outlook.com
 */
public class OkDownloader implements Downloader {
    private OkHttpClient client;
    private Request request;

    public OkDownloader(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public OkDownloader request(Request request) {
        this.request = request;
        return this;
    }

    @Override
    public void go(Callback callback) {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder()
                .url(request.url())
                .get();
        addHeader(builder, URI.create(request.url()));
        client.newCall(builder.build()).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(request, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    FileUtils.dumpAndClose(response.body().byteStream(), new FileOutputStream(request.savePath()));
                    callback.onSuccess(request);
                } catch (IOException e) {
                    callback.onFailure(request, e);
                }
            }
        });
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public OkDownloader clone() {
        return new OkDownloader(client);
    }

    private static void addHeader(okhttp3.Request.Builder builder, URI uri) {
        Map<String, List<String>> headers = BrowserVersion.CHROME.headers();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                builder.addHeader(key, value);
            }
        }
        builder.addHeader("Host", uri.getHost());
    }
}
