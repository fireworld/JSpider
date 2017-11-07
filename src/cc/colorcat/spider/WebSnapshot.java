package cc.colorcat.spider;

import cc.colorcat.spider.internal.Utils;

import java.nio.charset.Charset;

public final class WebSnapshot {
    private String url;
    private String resource;
    private Charset charset;

    public static WebSnapshot newSuccess(String url, String resource, Charset charset) {
        if (!Utils.isHttpUrl(url)) {
            throw new IllegalArgumentException("illegal url, url = " + url);
        }
        if (Utils.isEmpty(resource)) {
            throw new IllegalArgumentException("resource is empty");
        }
        if (charset == null) {
            throw new IllegalArgumentException("charset is null");
        }
        return new WebSnapshot(url, resource, charset);
    }

    private WebSnapshot(String url, String resource, Charset charset) {
        this.url = url;
        this.resource = resource;
        this.charset = charset;
    }

    public String url() {
        return this.url;
    }

    public String resouce() {
        return this.resource;
    }

    public Charset charset() {
        return this.charset;
    }

    @Override
    public String toString() {
        return "WebSnapshot{" +
                "url='" + url + '\'' +
                ", resource='" + resource + '\'' +
                ", charset=" + charset +
                '}';
    }
}
