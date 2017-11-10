package cc.colorcat.spider;

import java.util.List;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public interface EventListener {
    EventListener EMPTY_LISTENER = new EventListener() {
        @Override
        public void onStart(List<Scrap> seeds) {

        }

        @Override
        public void onSuccess(Scrap seed) {

        }

        @Override
        public void onFailed(Scrap seed) {

        }

        @Override
        public void onHandled(Scrap scrap) {

        }

        @Override
        public void onFinished(List<Scrap> allSeeds, List<Scrap> failedSeeds, List<Scrap> handledScraps) {

        }
    };

    void onStart(List<Scrap> seeds);

    void onSuccess(Scrap seed);

    void onFailed(Scrap seed);

    void onHandled(Scrap scrap);

    void onFinished(List<Scrap> allSeeds, List<Scrap> failedSeeds, List<Scrap> handledScraps);
}
