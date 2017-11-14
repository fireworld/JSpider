package cc.colorcat.jspider;


import cc.colorcat.jspider.internal.Utils;

import java.net.URI;
import java.nio.charset.Charset;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public final class WebSnapshot {
    private URI uri;
    private String resource;
    private Charset charset;

    public static WebSnapshot newSuccess(URI uri, String resource) {
        return newSuccess(uri, resource, Utils.UTF8);
    }

    public static WebSnapshot newSuccess(URI uri, String resource, Charset charset) {
        if (uri == null) {
            throw new IllegalArgumentException("uri == null");
        }
        if (Utils.isEmpty(resource)) {
            throw new IllegalArgumentException("resource is empty");
        }
        if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        }
        return new WebSnapshot(uri, resource, charset);
    }

    public static WebSnapshot newFailed(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri == null");
        }
        return new WebSnapshot(uri, "", Charset.defaultCharset());
    }

    private WebSnapshot(URI uri, String resource, Charset charset) {
        this.uri = uri;
        this.resource = resource;
        this.charset = charset;
    }

    public URI uri() {
        return this.uri;
    }

    public String resource() {
        return this.resource;
    }

    public Charset charset() {
        return this.charset;
    }

    public boolean isSuccess() {
        return !Utils.isEmpty(resource);
    }

    @Override
    public String toString() {
        return "WebSnapshot{" +
                "uri=" + uri +
                ", resource='" + resource + '\'' +
                ", charset=" + charset +
                '}';
    }
}
