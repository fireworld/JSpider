import cc.colorcat.jspider.Connection;
import cc.colorcat.jspider.WebSnapshot;
import cc.colorcat.jspider.internal.Utils;
import okhttp3.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

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
        Request.Builder builder = new Request.Builder()
                .url(uri.toString())
                .get();
        addHeader(builder, uri);
        Response response = client.newCall(builder.build()).execute();
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

    private static void addHeader(Request.Builder builder, URI uri) {
        Map<String, List<String>> headers = BrowserVersion.CHROME.headers();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                builder.addHeader(key, value);
            }
        }
        builder.addHeader("Host", uri.getHost());
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Connection clone() {
        return new OkConnection(client);
    }
}
