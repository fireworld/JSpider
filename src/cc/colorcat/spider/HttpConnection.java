package cc.colorcat.spider;

import cc.colorcat.spider.internal.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by cxx on ${DATA}.
 * xx.ch@outlook.com
 */
final class HttpConnection implements Connection {
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private WebSnapshot snapshot;
    private URI uri;

    HttpConnection() {

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
        this.snapshot = onGet(uri);
        return this.snapshot;
    }


    private static WebSnapshot onGet(URI uri) throws IOException {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            conn = (HttpURLConnection) new URL(uri.toString()).openConnection();
            conn.setRequestMethod("get");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                Charset charset = Utils.nullElse(Utils.parseCharset(conn.getContentType()), Utils.UTF8);
                is = conn.getInputStream();
                if (is != null) {
                    String result = Utils.toString(is, charset);
                    return WebSnapshot.newSuccess(uri, result, charset);
                }
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            Utils.close(is);
        }
        return WebSnapshot.newFailed(uri);
    }


    @Override
    public Connection clone() {
        return new HttpConnection();
    }
}
