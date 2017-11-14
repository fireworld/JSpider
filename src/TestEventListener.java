import cc.colorcat.jspider.EventListener;
import cc.colorcat.jspider.Scrap;
import cc.colorcat.jspider.Seed;
import cc.colorcat.jspider.internal.Log;

/**
 * Created by cxx on ${DATA}.
 * xx.ch@outlook.com
 */
public class TestEventListener implements EventListener {
    @Override
    public void onSuccess(Seed seed) {
        Log.f("spider success, seed = " + seed.toString());
    }

    @Override
    public void onFailure(Seed seed, Exception reason) {
        Log.s("spider failure, seed = " + seed.toString() + " \n\treason = " + reason.getMessage());
    }

    @Override
    public void onReachedMaxDepth(Seed seed) {
        Log.w("spider reached max depth, seed = " + seed.toString());
    }

    @Override
    public void onHandled(Scrap scrap) {
        Log.f("spider handled, scrap = " + scrap);
    }
}
