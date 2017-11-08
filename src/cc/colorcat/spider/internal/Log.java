package cc.colorcat.spider.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    private static final Logger LOGGER;

    static {
        LOGGER = Logger.getLogger("JSpider");
        LOGGER.setLevel(Level.ALL);
    }

    public static void f(String msg) {
        log(Level.FINEST, msg);
    }

    public static void d(String msg) {
        log(Level.CONFIG, msg);
    }

    public static void i(String msg) {
        log(Level.INFO, msg);
    }

    public static void w(String msg) {
        log(Level.WARNING, msg);
    }

    public static void e(Throwable t) {
        t.printStackTrace();
    }

    private static void log(Level level, String msg) {
        LOGGER.log(level, msg);
    }
}
