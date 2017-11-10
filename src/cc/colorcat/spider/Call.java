package cc.colorcat.spider;

import java.io.IOException;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public interface Call extends Runnable {

    int count();

    void incrementCount();

    Scrap seed();

    void execute() throws IOException;

    interface Factory {

        Call newCall(Scrap seed);
    }
}
