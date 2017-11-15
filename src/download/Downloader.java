package download;

/**
 * Created by cxx on 17-11-15.
 * xx.ch@outlook.com
 */
public interface Downloader extends Cloneable {

    Downloader request(Request request);

    void go(Callback callback);

    Downloader clone();

    interface Callback {

        void onSuccess(Request request);

        void onFailure(Request request, Exception reason);
    }
}
