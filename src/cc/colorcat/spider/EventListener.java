package cc.colorcat.spider;

import java.util.List;

public interface EventListener {
    EventListener EMPTY_LISTENER = new EventListener() {
        @Override
        public void onCrawlStart(List<Scrap> seeds) {

        }

        @Override
        public void onCrawlSuccess(Scrap scrap) {

        }

        @Override
        public void onCrawlFailed(Scrap scrap) {

        }

        @Override
        public void onCrawledData(Scrap data) {

        }

        @Override
        public void onCrawlFinished(List<Scrap> all, List<Scrap> failed) {

        }
    };

    void onCrawlStart(List<Scrap> seeds);

    void onCrawlSuccess(Scrap scrap);

    void onCrawlFailed(Scrap scrap);

    void onCrawledData(Scrap data);

    void onCrawlFinished(List<Scrap> all, List<Scrap> failed);
}
