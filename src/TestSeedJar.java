import cc.colorcat.jspider.Seed;
import cc.colorcat.jspider.SeedJar;
import cc.colorcat.jspider.internal.Log;

import java.util.Collections;
import java.util.List;

/**
 * Created by cxx on ${DATA}.
 * xx.ch@outlook.com
 */
public class TestSeedJar implements SeedJar {
    @Override
    public void save(List<Seed> success, List<Seed> failed, List<Seed> reachedMaxDepth) {
        Log.i("all finish, success:");
        log(success);

        Log.i("all finish, failed:");
        log(failed);


        Log.i("all finish, reached max depth:");
        log(reachedMaxDepth);
    }

    @Override
    public List<Seed> load() {
        return Collections.emptyList();
    }

    private static void log(List<Seed> seeds) {
        for (Seed seed : seeds) {
            Log.i("\t\t" + seed);
        }
    }
}
