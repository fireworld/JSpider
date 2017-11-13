package cc.colorcat.spider;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public interface Call extends Runnable {

    int count();

    void incrementCount();

    Seed seed();

    void execute();

    interface Factory {

        Call newCall(Seed seed);
    }
}
