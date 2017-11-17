package cc.colorcat.jspider.test.download;


import java.io.File;
import java.net.URI;
import java.util.*;

/**
 * Created by cxx on 2017-11-15.
 * xx.ch@outlook.com
 */
public final class Task {
    private URI uri;
    private Method method;
    private Map<String, List<String>> headers;
    private File savePath;
    private int retryCount = 0;

    public static Task create(String uri, File savePath) {
        return create(uri, savePath, Method.GET, Collections.emptyMap());
    }

    public static Task create(String uri, File savePath, Method method) {
        return create(uri, savePath, method, Collections.emptyMap());
    }

    public static Task create(String uri, File savePath, Map<String, List<String>> headers) {
        return create(uri, savePath, Method.GET, headers);
    }

    public static Task create(String uri, File savePath, Method method, Map<String, List<String>> headers) {
        return create(URI.create(uri), savePath, method, headers);
    }

    public static Task create(URI uri, File savePath) {
        return create(uri, savePath, Method.GET, Collections.emptyMap());
    }

    public static Task create(URI uri, File savePath, Map<String, List<String>> headers) {
        return create(uri, savePath, Method.GET, headers);
    }

    public static Task create(URI uri, File savePath, Method method) {
        return create(uri, savePath, method, Collections.emptyMap());
    }

    public static Task create(URI uri, File savePath, Method method, Map<String, List<String>> headers) {
        Objects.requireNonNull(uri);
        Objects.requireNonNull(savePath);
        Objects.requireNonNull(method);
        Objects.requireNonNull(headers);
        FileUtils.createParentDirsIfNotExists(savePath);
        return new Task(uri, savePath, method, headers);
    }

    private Task(URI uri, File savePath, Method method, Map<String, List<String>> headers) {
        this.uri = uri;
        this.savePath = savePath;
        this.method = method;
        this.headers = new HashMap<>(headers);
    }

    public URI uri() {
        return uri;
    }

    public Method method() {
        return method;
    }

    public String methodWithString() {
        return method.name();
    }

    public Map<String, List<String>> headers() {
        return Collections.unmodifiableMap(headers);
    }

    public File savePath() {
        return savePath;
    }

    public Task header(String name, String value) {
        Objects.requireNonNull(name, "name == null");
        Objects.requireNonNull(value, "value == null");
        List<String> values = headers.computeIfAbsent(name, k -> new ArrayList<>());
        values.add(value);
        return this;
    }

    int retryCount() {
        return retryCount;
    }

    void incrementRetryCount() {
        ++retryCount;
    }

    @Override
    public String toString() {
        return "Task{" +
                "uri=" + uri +
                ", method=" + method +
                ", headers=" + headers +
                ", savePath=" + savePath +
                ", retryCount=" + retryCount +
                '}';
    }
}
