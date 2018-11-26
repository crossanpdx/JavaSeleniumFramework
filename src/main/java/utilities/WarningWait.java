package utilities;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.SystemClock;

//Based on FluentWait code

/**
 * A FluentWait that logs a message upon beginning waiting, and a warning after waiting more than 5 seconds
 */
public class WarningWait<T> extends FluentWait<T>
{
    private final WarningSleeper sleeper;
    private Supplier<String> messageSupplier = null;

    private WarningWait(T input, Clock clock, WarningSleeper sleeper)
    {
        super(input, clock, sleeper);
        this.sleeper = sleeper;
    }

    public WarningWait(T input)
    {
        this(input, new SystemClock(), new WarningSleeper());
    }

    @Override
    public FluentWait<T> withMessage(String message)
    {
        return withMessage(() -> message);
    }

    @Override
    public FluentWait<T> withMessage(Supplier<String> messageSupplier)
    {
        this.messageSupplier = messageSupplier;
        return super.withMessage(messageSupplier);
    }

    @Override
    public <V> V until(Function<? super T, V> isTrue)
    {
        String message = messageSupplier != null ? messageSupplier.get() : "for " + isTrue.toString();
        LogManager.debug("Waiting " + message);
        sleeper.startWarnTime(Duration.of(5, ChronoUnit.SECONDS), "5 seconds", message);
        return super.until(isTrue);
    }

    private static class WarningSleeper implements Sleeper
    {
        private final Sleeper delegate = Sleeper.SYSTEM_SLEEPER;
        private final Clock clock = new SystemClock();
        private long warnTime = -1;
        private String durationDescription;
        private String message = null;

        @Override
        public void sleep(Duration duration) throws InterruptedException
        {
            delegate.sleep(duration);
            if (warnTime != -1 && !clock.isNowBefore(warnTime))
            {
                warnTime = -1;
                LogManager.warn("Spent more than " + durationDescription + " waiting " + message);
            }
        }

        public void startWarnTime(Duration duration, String durationDescription, String message)
        {
            warnTime = clock.laterBy(duration.toMillis());
            this.durationDescription = durationDescription;
            this.message = message;
        }
    }
}
