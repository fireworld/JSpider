package cc.colorcat.spider;

import java.util.List;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public interface EventListener {
    EventListener EMPTY_LISTENER = new EventListener() {
        @Override
        public void onStart(List<Seed> seeds) {

        }

        @Override
        public void onSuccess(Seed seed) {

        }

        @Override
        public void onFailed(Seed seed, Exception reason) {

        }

        @Override
        public void onHandled(Scrap scrap) {

        }

        @Override
        public void onReachedMaxDepth(Seed seed) {

        }

        @Override
        public void onFinished(List<Seed> allSeeds, List<Seed> failedSeeds, List<Scrap> handledScraps) {

        }
    };

    void onStart(List<Seed> seeds);

    void onSuccess(Seed seed);

    void onFailed(Seed seed, Exception reason);

    void onHandled(Scrap scrap);

    void onReachedMaxDepth(Seed seed);

    void onFinished(List<Seed> allSeeds, List<Seed> failedSeeds, List<Scrap> handledScraps);
}
