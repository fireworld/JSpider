package cc.colorcat.jspider.test.download;

/**
 * Created by cxx on 2017-11-15.
 * xx.ch@outlook.com
 */
public interface Downloader extends Cloneable {

    Downloader task(Task task);

    void go(Callback callback);

    Downloader clone();

    interface Callback {

        void onSuccess(Task task);

        void onFailure(Task task, Exception reason);
    }
}
