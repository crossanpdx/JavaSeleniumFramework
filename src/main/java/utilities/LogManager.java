package utilities;

import com.google.common.collect.EvictingQueue;
import java.text.DateFormat;
import java.util.Date;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * Log only the messages at LOG_LEVEL or above, but save up to RECENT_MESSAGES_LIMIT recent messages from all levels, and display those messages if the test fails
 */
public class LogManager
{
    private static final Level LOG_LEVEL = Level.DEBUG;
    private static final int RECENT_MESSAGES_LIMIT = 20;

    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(LogManager.class.getName());
    private static final EvictingQueue<TimestampedMessage> allRecentMessages = EvictingQueue.create(RECENT_MESSAGES_LIMIT);
    private static int warningsCount = 0;

    static
    {
        Configurator.setAllLevels(logger.getName(), LOG_LEVEL);
    }

    private static class TimestampedMessage
    {
        //This isn't exactly the same format as the real log4j. But, whatever
        private static final DateFormat dateFormat = DateFormat.getDateTimeInstance();
        public final String message;
        public final Date timestamp;
        public final Level level;
        public final String threadName;

        public TimestampedMessage(String message, Date timestamp, Level level, String threadName)
        {
            this.message = message;
            this.timestamp = timestamp;
            this.level = level;
            this.threadName = threadName;
        }

        @Override
        public String toString()
        {
            return String.format("[%-5s] %s [%s] %s - %s", level, dateFormat.format(timestamp), threadName, LogManager.class.getSimpleName(), message);
        }
    }

    public static void dumpRecentMessages()
    {
        System.err.println("All recent log messages before crash:");
        allRecentMessages.forEach(System.err::println);
    }

    public static int getWarningsCount()
    {
        return warningsCount;
    }

    public static void reset()
    {
        allRecentMessages.clear();
        warningsCount = 0;
    }

    public static void log(Level level, String message)
    {
        logger.log(level, message);
        allRecentMessages.add(new TimestampedMessage(message, new Date(), level, Thread.currentThread().getName()));
        if (level.isMoreSpecificThan(Level.WARN))
        {
            warningsCount++;
        }
    }

    public static void info(String message)
    {
        log(Level.INFO, message);
    }

    public static void warn(String message)
    {
        log(Level.WARN, message);
    }

    public static void error(String message)
    {
        log(Level.ERROR, message);
    }

    public static void fatal(String message)
    {
        log(Level.FATAL, message);
    }

    public static void debug(String message)
    {
        log(Level.DEBUG, message);
    }

    public static void trace(String message)
    {
        log(Level.TRACE, message);
    }

    public static String describeElement(WebElement element)
    {
        try
        {
            return element.getTagName()
                + (element.getAttribute("id").isEmpty() ? "" : "#" + element.getAttribute("id"))
                + " \"" + element.getText().replace('\n', '¶') + "\""
                + " class=\"" + element.getAttribute("className") + "\"";
        }
        catch (StaleElementReferenceException | org.openqa.selenium.UnhandledAlertException a)
        {
            return "<stale element>";
        }
    }
}
