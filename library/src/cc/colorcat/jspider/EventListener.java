package cc.colorcat.jspider;

/**
 * Created by cxx on 2017-11-9.
 * xx.ch@outlook.com
 */
public interface EventListener {
    EventListener EMPTY_LISTENER = new EventListener() {
        @Override
        public void onSuccess(Seed seed) {

        }

        @Override
        public void onFailure(Seed seed, Exception reason) {

        }

        @Override
        public void onReachedMaxDepth(Seed seed) {

        }

        @Override
        public void onHandled(Scrap scrap) {

        }
    };

    void onSuccess(Seed seed);

    void onFailure(Seed seed, Exception reason);

    void onReachedMaxDepth(Seed seed);

    void onHandled(Scrap scrap);
}
