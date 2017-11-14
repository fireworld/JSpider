import cc.colorcat.jspider.Connection;
import cc.colorcat.jspider.WebSnapshot;
import cc.colorcat.jspider.internal.Utils;
import okhttp3.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public class OkConnection implements Connection {
    private OkHttpClient client;
    private WebSnapshot snapshot;

    OkConnection(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public WebSnapshot get(URI uri) throws IOException {
        if (!Utils.isHttpUrl(uri)) {
            throw new UnsupportedOperationException("Unsupported uri, uri = " + uri.toString());
        }
        if (snapshot != null && snapshot.isSuccess() && snapshot.uri().equals(uri)) {
            return snapshot;
        }
        return this.snapshot = doGet(uri);
    }

    private WebSnapshot doGet(URI uri) throws IOException {
        Request request = new Request.Builder()
                .url(uri.toString())
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36")
                .get()
                .build();
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
