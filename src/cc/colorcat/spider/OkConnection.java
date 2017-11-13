package cc.colorcat.spider;

import cc.colorcat.spider.internal.Utils;
import okhttp3.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public class OkConnection implements Connection {
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private OkHttpClient client;
    private WebSnapshot snapshot;
    private URI uri;

    public OkConnection() {
        client = new OkHttpClient.Builder()
                .build();
    }

    public OkConnection(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public WebSnapshot get(URI uri) throws IOException {
        String scheme = Utils.nullElse(uri.getScheme(), "").toLowerCase();
        if (!HTTP.equals(scheme) && !HTTPS.equals(scheme)) {
            throw new UnsupportedOperationException("Unsupported uri, uri = " + uri.toString());
        }
        if (snapshot != null && snapshot.isSuccess() && uri.equals(this.uri)) {
            return snapshot;
        }
        this.uri = uri;
        this.snapshot = doGet(uri);
        return this.snapshot;
    }

    private WebSnapshot doGet(URI uri) throws IOException {
        Request request = new Request.Builder().url(uri.toString()).get().build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            ResponseBody body = response.body();
            if (body != null) {
                try {
                    MediaType mediaType = body.contentType();
                    Charset charset = mediaType != null ? mediaType.charset(Utils.UTF8) : Utils.UTF8;
                    return WebSnapshot.newSuccess(uri, body.string(), charset);
                } finally {
                    Utils.close(body);
                }
            }
        }
        return WebSnapshot.newFailed(uri);
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Connection clone() {
        return new OkConnection(client);
    }
}
