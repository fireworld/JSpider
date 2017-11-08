package cc.colorcat.spider;

import java.io.IOException;
import java.net.URI;

public interface Connector extends Cloneable {

    WebSnapshot get(URI uri) throws IOException;

    Connector clone();
}
