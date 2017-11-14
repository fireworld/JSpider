package cc.colorcat.jspider.internal;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by cxx on 17-11-9.
 * xx.ch@outlook.com
 */
public class Log {
    private static final Logger LOGGER;

    static {
        LOGGER = Logger.getLogger("JSpider");
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        LOGGER.addHandler(handler);
        LOGGER.setLevel(Level.ALL);
    }

    public static void f(String msg) {
        log(Level.FINE, msg);
    }

    public static void i(String msg) {
        log(Level.INFO, msg);
    }

    public static void w(String msg) {
        log(Level.WARNING, msg);
    }

    public static void s(String msg) {
        log(Level.SEVERE, msg);
    }

    public static void e(Throwable t) {
        t.printStackTrace();
    }

    private static void log(Level level, String msg) {
        LOGGER.log(level, msg);
    }
}
