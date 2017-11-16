package cc.colorcat.jspider.test;

import cc.colorcat.jspider.Seed;
import cc.colorcat.jspider.SeedJar;
import cc.colorcat.jspider.internal.Log;
import com.google.gson.reflect.TypeToken;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by cxx on 2017/11/14.
 * xx.ch@outlook.com
 */
public class TestSeedJar implements SeedJar {
    private static final File savePath = new File(Main.SAVE_DIR, "SeedJar.data");

    @Override
    public void save(List<Seed> success, List<Seed> failed, List<Seed> reachedMaxDepth) {
        Log.i("all finish, success:");
        log(success);

        Log.i("all finish, failed:");
        log(failed);

        Log.i("all finish, reached max depth:");
        log(reachedMaxDepth);

        List<Seed> seeds = Stream.concat(failed.stream(), reachedMaxDepth.stream())
                .map(Seed::newSeedWithResetDepth)
                .collect(Collectors.toList());
        save(seeds);
    }

    @Override
    public List<Seed> load() {
        return read();
    }

    private static void log(List<Seed> seeds) {
        if (seeds.isEmpty()) {
            Log.i("\t\tempty");
        } else {
            for (Seed seed : seeds) {
                Log.i("\t\t" + seed);
            }
        }
    }

    private static void save(List<Seed> seeds) {
        try {
            String json = JsonUtils.toJson(seeds);
            Okio.buffer(Okio.sink(savePath)).writeUtf8(json).flush();
        } catch (IOException e) {
            Log.e(e);
        }
    }

    private static List<Seed> read() {
        try {
            String json = Okio.buffer(Okio.source(savePath)).readUtf8();
            return JsonUtils.fromJson(json, new TypeToken<List<Seed>>() {
            }.getType());
        } catch (IOException e) {
            Log.e(e);
            return Collections.emptyList();
        }
    }
}
