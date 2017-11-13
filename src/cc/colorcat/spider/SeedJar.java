package cc.colorcat.spider;

import java.util.Collections;
import java.util.List;

/**
 * Created by cxx on ${DATA}.
 * xx.ch@outlook.com
 */
public interface SeedJar {
    SeedJar NO_SEEDS = new SeedJar() {
        @Override
        public void save(List<Seed> success, List<Seed> failed, List<Seed> reachedMaxDepth) {

        }

        @Override
        public List<Seed> load() {
            return Collections.emptyList();
        }
    };

    void save(List<Seed> success, List<Seed> failed, List<Seed> reachedMaxDepth);

    List<Seed> load();
}
