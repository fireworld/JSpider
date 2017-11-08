package cc.colorcat.spider;

import java.io.IOException;
import java.net.URI;

public interface Connector extends Cloneable {

    WebSnapshot get(JSpider spider, URI uri) throws IOException;

    Connector clone();
}
