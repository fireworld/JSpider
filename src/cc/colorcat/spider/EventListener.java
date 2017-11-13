package cc.colorcat.spider;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public interface EventListener {
    EventListener EMPTY_LISTENER = new EventListener() {
//        @Override
//        public void onStart(List<Seed> seeds) {
//
//        }

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

//        @Override
//        public void onFinished(List<Seed> allSeeds, List<Seed> failedSeeds, List<Scrap> handledScraps) {
//
//        }
    };

//    void onStart(List<Seed> seeds);

    void onSuccess(Seed seed);

    void onFailure(Seed seed, Exception reason);

    void onReachedMaxDepth(Seed seed);

    void onHandled(Scrap scrap);

//    void onFinished(List<Seed> allSeeds, List<Seed> failedSeeds, List<Scrap> handledScraps);
}
