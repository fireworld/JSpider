package cc.colorcat.spider;

import cc.colorcat.spider.internal.Utils;
import okhttp3.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

public class OkConnector implements Connector {
    private OkHttpClient client;

    public OkConnector() {
        client = new OkHttpClient.Builder().build();
    }

    private OkConnector(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public WebSnapshot get(JSpider spider, URI uri) throws IOException {
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
    public OkConnector clone() {
        return new OkConnector(client);
    }
}
