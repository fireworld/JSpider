package cc.colorcat.jspider.test;

import cc.colorcat.jspider.Connection;
import cc.colorcat.jspider.WebSnapshot;
import cc.colorcat.jspider.internal.UserAgent;
import cc.colorcat.jspider.internal.Utils;
import okhttp3.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by cxx on 2017-11-9.
 * xx.ch@outlook.com
 */
public class OkConnection implements Connection {
    private final OkHttpClient client;
    private final Charset defaultCharset;
    private WebSnapshot snapshot;

    OkConnection(OkHttpClient client) {
        this(client, Utils.UTF8);
    }

    public OkConnection(OkHttpClient client, Charset defaultCharset) {
        this.client = client;
        this.defaultCharset = defaultCharset;
    }

    @Override
    public WebSnapshot get(URI uri) throws IOException {
        if (!Utils.isHttpUrl(uri)) {
            throw new UnsupportedOperationException("Unsupported uri, uri = " + uri.toString());
        }
        if (snapshot != null && snapshot.isSuccess() && snapshot.uri().equals(uri)) {
            return snapshot;
        }
        return this.snapshot = doGet(uri, defaultCharset);
    }

    private WebSnapshot doGet(URI uri, Charset defaultCharset) throws IOException {
        Request request = new Request.Builder()
                .url(uri.toString())
                .header(UserAgent.NAME, UserAgent.Value.CHROME_MAC)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            ResponseBody body = response.body();
            if (body != null) {
                try {
                    MediaType mediaType = body.contentType();
                    Charset charset = mediaType != null ? mediaType.charset(defaultCharset) : defaultCharset;
                    return WebSnapshot.newSuccess(uri, new String(body.bytes(), charset), charset);
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
        return new OkConnection(client, defaultCharset);
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
