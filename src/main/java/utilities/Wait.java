package utilities;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Wait extends PageUtility
{

    public Wait(Page page)
    {
        super(page);
    }

    public WebElement waiter(By by) throws TimeoutException
    {
        assertNotClosed();
        return waiter(by, driver);
    }

    public void waitForPageLoaded(Runnable doNavigate)
    {
        assertNotClosed();
        waitForPageLoaded(doNavigate, driver);
    }

    public FluentWait<RemoteWebDriver> getWaiter()
    {
        return getWaiter(driver);
    }

    //Shortcut method
    public <T> T until(Function<WebDriver, T> condition) throws TimeoutException
    {
        return getWaiter().until(condition);
    }

    public <T> T until(String description, Function<WebDriver, T> condition) throws TimeoutException
    {
        return getWaiter().withMessage(description).until(condition);
    }

    public static <T> FluentWait<T> getWaiter(T context)
    {
        FluentWait<T> wait = new WarningWait<>(context);
        wait.pollingEvery(Duration.of(150, ChronoUnit.MILLIS));
        wait.withTimeout(Duration.of(30, ChronoUnit.SECONDS)); // If an operation takes more than 30 seconds, that's a bug
        return wait;
    }

    public static WebElement waiter(By by, SearchContext context) throws TimeoutException
    {
        String description = "to find element " + by;
        WebElement element = getWaiter(context).withMessage(description).ignoring(NoSuchElementException.class).until(context1 -> context1.findElement(by));
        LogManager.trace("Found element " + LogManager.describeElement(element));
        return element;
    }
    
    /**
     * Currently, this method always waits at least 1 second, which is enough time for most actions to complete,
     * but you should not call waitForPageLoaded unless you're actually waiting for a page to load.
     * This method is generally inferior to calling {@link #waitForPageLoaded(Runnable, RemoteWebDriver)} for real page loads
     * For fake page loads (JS-driven contents replacement), you should instead use until(ExpectedConditions.elementGone(root)), where root is an element that should be removed during the load (like {@link Navigate#via}
     * This method will be removed in the future
     */
    public static void waitForPageLoaded(RemoteWebDriver driver)
    {
        LogManager.warn("waitForPageLoaded(driver) is deprecated. Use waitForPageLoaded(Runnable, driver) instead.");
        try
        {
            Thread.sleep(1000);
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(pageFinishedLoading);
        }
        catch (TimeoutException | InterruptedException ex)
        {
            LogManager.error(">>> Timeout waiting for Page Load Request to complete.");
        }
    }

    /**
     * Perform doNavigate and then accurately wait for page loaded
     * Only works for *real* page loads, such as when logging in. In some products, most "page loads" are actually
     * just JS-driven replacement of the entire page content, where only the dimmer runs
     * Unlike {@link #waitForPageLoaded(RemoteWebDriver)}, will always wait until a page actually loads, will fail if no load happens, and won't wait too long.
     */
    public static void waitForPageLoaded(Runnable doNavigate, RemoteWebDriver driver)
    {
        WebElement root = driver.findElement(By.xpath("/html"));
        doNavigate.run();
        getWaiter(driver).until(elementGone(root));
        getWaiter(driver).until(pageFinishedLoading);
    }

    public static final ExpectedCondition<Boolean> pageFinishedLoading = driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");

    /**
     * As ExpectedConditions.stalenessOf, but does not throw an error if the element is in a non-active iframe (Chrome never throws this error)
     */
    public static ExpectedCondition<Boolean> elementGone(WebElement element)
    {
        ExpectedCondition<Boolean> seleniumStaleness = ExpectedConditions.stalenessOf(element);
        return driver ->
        {
            try
            {
                return seleniumStaleness.apply(driver);
            }
            catch (NoSuchElementException ex)
            {
                if (((HasCapabilities) driver).getCapabilities().getBrowserName().equals("firefox")
                    && ex.getMessage().split("\n", 2)[0].contains("Web element reference not seen before: "))
                {
                    return true;
                }
                else
                {
                    throw ex;
                }
            }
        };
    }
}
