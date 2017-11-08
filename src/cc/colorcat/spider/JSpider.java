package cc.colorcat.spider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class JSpider {
    private Connector connector = new OkConnector();
    private Dispatcher dispatcher;

    {
        dispatcher = new Dispatcher(this, Executors.newCachedThreadPool());
    }

    public void seed(List<Scrap<?>> scraps) {
        dispatcher.start(scraps);
    }

    public int maxRunning() {
        return 10;
    }

    public int maxTry() {
        return 3;
    }

    public Connector connector() {
        return this.connector;
    }

    public void printConnectFailed(Set<Scrap<?>> failed) {
        System.out.println("------------------------connect failed------------------------");
        System.out.println(failed);
    }

    public void printParseFailed(Set<Scrap<?>> failed) {
        System.out.println("------------------------connect failed------------------------");
        System.out.println(failed);
    }
}
