package cc.colorcat.spider;

import java.net.URI;

public interface Listener {

    boolean filter(Object extra);

    void onSuccess(Scrap scrap);

    void onFailure(URI uri);
}
