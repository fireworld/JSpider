package cc.colorcat.jspider;

import java.io.IOException;
import java.net.URI;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public interface Connection extends Cloneable {

    WebSnapshot get(URI uri) throws IOException;

    Connection clone();
}
