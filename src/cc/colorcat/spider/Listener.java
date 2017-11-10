package cc.colorcat.spider;

import java.net.URI;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public interface Listener {

    boolean filter(Object extra);

    void onSuccess(Scrap scrap);

    void onFailure(URI uri);
}
