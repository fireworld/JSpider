package cc.colorcat.spider;

import java.io.IOException;

public interface Call extends Runnable {

    int count();

    Scrap seed();

    void execute() throws IOException;

    interface Factory {

        Call newCall(Scrap seed);
    }
}
