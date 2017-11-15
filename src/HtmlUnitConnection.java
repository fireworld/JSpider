import cc.colorcat.jspider.Connection;
import cc.colorcat.jspider.WebSnapshot;

import java.io.IOException;
import java.net.URI;

/**
 * Created by cxx on 2017/11/15.
 * xx.ch@outlook.com
 */
public class HtmlUnitConnection implements Connection {
    @Override
    public WebSnapshot get(URI uri) throws IOException {
        return null;
    }

    @Override
    public Connection clone() {
        return null;
    }
}
