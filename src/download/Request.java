package download;


import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by cxx on 17-11-15.
 * xx.ch@outlook.com
 */
public final class Request {
    private String url;
    private Method method;
    private Map<String, List<String>> headers;
    private File savePath;
    private int retryCount = 0;

    public static Request create(String url, File savePath) {
        return create(url, Method.GET, Collections.emptyMap(), savePath);
    }

    public static Request create(String url, Method method, Map<String, List<String>> headers, File savePath) {
        if (url == null || !url.toLowerCase().matches("^(http)(s)?://(.)*")) {
            throw new IllegalArgumentException("illegal url, url = " + url);
        }
        Objects.requireNonNull(method);
        Objects.requireNonNull(headers);
        Objects.requireNonNull(savePath);
        FileUtils.createParentDirsIfNotExists(savePath);
        return new Request(url, method, headers, savePath);
    }

    private Request(String url, Method method, Map<String, List<String>> headers, File savePath) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.savePath = savePath;
    }

    public String url() {
        return url;
    }

    public Method method() {
        return method;
    }

    public String methodWithString() {
        return method.name();
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public File savePath() {
        return savePath;
    }

    int retryCount() {
        return retryCount;
    }

    void incrementRetryCount() {
        ++retryCount;
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", method=" + method +
                ", headers=" + headers +
                ", savePath=" + savePath +
                ", retryCount=" + retryCount +
                '}';
    }
}
