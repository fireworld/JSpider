import cc.colorcat.jspider.Seed;
import cc.colorcat.jspider.SeedJar;

import java.util.List;

/**
 * Created by cxx on ${DATA}.
 * xx.ch@outlook.com
 */
public class TestSeedJar implements SeedJar {
    @Override
    public void save(List<Seed> success, List<Seed> failed, List<Seed> reachedMaxDepth) {

    }

    @Override
    public List<Seed> load() {
        return null;
    }
}
