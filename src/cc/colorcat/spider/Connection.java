package cc.colorcat.spider;

import java.io.IOException;
import java.net.URI;

public interface Connection extends Cloneable {

    WebSnapshot get(URI uri) throws IOException;

    Connection clone();
}
