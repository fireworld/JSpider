package cc.colorcat.jspider;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public interface Call extends Runnable {

    int retryCount();

    void incrementRetryCount();

    Seed seed();

    void execute();

    interface Factory {

        Call newCall(Seed seed);
    }
}
