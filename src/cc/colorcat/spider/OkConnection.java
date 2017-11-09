package cc.colorcat.spider;

import cc.colorcat.spider.internal.Utils;
import okhttp3.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

public class OkConnection implements Connection {
    private OkHttpClient client;
    private WebSnapshot snapshot;

    public OkConnection() {
        client = new OkHttpClient.Builder().build();
    }

    public OkConnection(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public WebSnapshot get(URI uri) throws IOException {
        if (snapshot != null && snapshot.isSuccess()) return snapshot;
        Request request = new Request.Builder().url(uri.toString()).get().build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            ResponseBody body = response.body();
            if (body != null) {
                try {
                    MediaType mediaType = body.contentType();
                    Charset charset = mediaType != null ? mediaType.charset(Utils.UTF8) : Utils.UTF8;
                    snapshot = WebSnapshot.newSuccess(uri, body.string(), charset);
                } finally {
                    Utils.close(body);
                }
            }
        }
        if (snapshot == null) snapshot = WebSnapshot.newFailed(uri);
        return snapshot;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Connection clone() {
        return new OkConnection(client);
    }
}
