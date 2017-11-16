package cc.colorcat.jspider.test.download;

/**
 * Created by cxx on 17-11-15.
 * xx.ch@outlook.com
 */
public enum Method {
    GET, HEAD, TRACE, OPTIONS, POST, PUT, DELETE;

    public boolean needBody() {
        switch (this) {
            case POST:
            case PUT:
            case DELETE:
                return true;
            default:
                return false;
        }
    }
}
