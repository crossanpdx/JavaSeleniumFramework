package utilities;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Utility for performing page navigation.
 * Because page navigation requires slightly different methods in different products, this class is abstract.
 * Pages should use an appropriate subclass for their product.
 * Using different classes of Navigate allows pageObjects of the same product to share the same Navigate code
 * even when some are TablePage and some are FormPage, while at the same time allowing pageObjects of different
 * products to share TablePage and FormPage code.
 */
public abstract class Navigate extends PageUtility
{
    public Navigate(Page page)
    {
        super(page);
    }

    /**
     * Return an element on the page which, when removed, indicates a new page has loaded
     * In general, you want the largest (closest to the root of the HTML tree) element that allows {@link #via} to succeed
     */
    protected WebElement getRootElement()
    {
        return driver.findElement(By.xpath("/html"));
    }

    /**
     * If merely being loaded does not imply this page is ready, wait for the page to be ready
     */
    public void waitForReady()
    {
        waiter().until(Wait.pageFinishedLoading);
    }

    /**
     * Navigate to another page and wait for it to be ready
     *
     * @param doNavigate
     *     What to do to navigate
     * @param newPage
     *     The page you expect to be directed to.
     * @return newPage, only after it is ready to accept commands.
     */
    public <T extends Page> T via(Runnable doNavigate, T newPage)
    {
        WebElement root = getRootElement();
        Runnable waitForPageRootGone = () ->
        {
            try
            {
                retryIfInspectorBug(() -> waiter().until(Wait.elementGone(root)));
            }
            catch (TimeoutException ex)
            {
                String message = "Page root did not disappear! You need to override getRootElement to select a more specific element (deeper in the page).\n"
                    + "getRootElement represents an element that is always present, but always removed by navigation. "
                    + "For SmokeTests, in RSAdmin, the header is never unloaded during navigation; only the contents of app-content are dynamically replaced by Javascript.\n"
                    + "To figure out which element you need to override, open Inspect Element on something inside the page, then perform navigation. "
                    + "Elements that are replaced during the page load will disappear, causing their parent elements to flash purple and hide their contents. "
                    + "Set the root element to the outer-most element that disappeared.";
                throw new RuntimeException(message, ex);
            }
        };
        return navigateAndWait(doNavigate, waitForPageRootGone, newPage);
    }

    public <T extends Page> T viaClickNewIFrame(By link, T newPage, By iframe, boolean buttonOutsideOfFrame)
    {
        if (buttonOutsideOfFrame)
        {
            driver.switchTo().defaultContent();
            element().click(link);
        }
        else
        {
            element().click(link);
            driver.switchTo().defaultContent();
        }
        WebElement frame = driver.findElement(By.tagName("iframe"));
        return navigateAndWait(() -> waiter().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe)), () -> waiter().until(ExpectedConditions.invisibilityOf(frame)), newPage);
    }

    public <T extends Page> T viaClickNewSection(WebElement link, T newPage, WebElement section)
    {
        return navigateAndWait(() -> element().click(link), () -> waiter().until(ExpectedConditions.visibilityOf(section)), newPage);
    }

    private <T extends Page> T navigateAndWait(Runnable doNavigate, Runnable waitForOldPageGone, T newPage)
    {
        LogManager.info(">>> Navigating to " + newPage.getClass().getSimpleName());
        assertNotClosed();
        doNavigate.run();
        close();
        waitForOldPageGone.run();
        waitForReady(newPage);
        return newPage;
    }

    /**
     * Follow a link to another page.
     * You cannot continue to use this Page after calling this method.
     *
     * @param link
     *     The button/link to click.
     * @param newPage
     *     The page you expect to be directed to.
     * @return newPage, only after it is ready to accept commands.
     */
    public <T extends Page> T viaClick(WebElement link, T newPage)
    {
        return via(() -> element().click(link), newPage);
    }

    public <T extends Page> T viaClickToNewWindow(WebElement link, T newPage)
    {
        return via(() ->
        {
            int windows = driver.getWindowHandles().size();
            element().click(link);
            waiter().until(d -> driver.getWindowHandles().size() != windows);
            window().tabFocus();
        }, newPage);
    }

    public <T extends Page> T viaWindowToIframe(WebElement link, T newPage, By iframe, boolean alert)
    {
        return via(() ->
        {
            element().click(link);
            if (alert)
            {
                window().alertConfirm();
                waiter().until(ExpectedConditions.numberOfWindowsToBe(1));
            }
            window().tabFocus();
            if (driver.getCapabilities().getBrowserName().equals("firefox"))
            {
                driver.switchTo().defaultContent(); //Chrome, unlike Firefox, resets you to the outer frame when switching back to a window
            }
            waiter().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe));
        }, newPage);
    }

    public <T extends Page> T viaNewFrameThatRefreshes(By link, T newPage, String iframe, By elementToBeStaleBy)
    {
        driver.switchTo().defaultContent();
        driver.switchTo().frame(iframe);
        WebElement elementToGoStale = driver.findElement(elementToBeStaleBy);
        driver.switchTo().defaultContent();
        element().click(link);
        driver.switchTo().frame(iframe);
        waiter().until(ExpectedConditions.stalenessOf(elementToGoStale));
        waitForReady(newPage);
        return newPage;
    }


    public void refresh(boolean acceptAlert)
    {
        LogManager.info(">>> Refresh " + page.getClass().getSimpleName());
        assertNotClosed();
        waiter().waitForPageLoaded(() ->
        {
            driver.navigate().refresh();
            if (acceptAlert)
            {
                window().alertConfirm();
            }
            assertFalse("Refresh must not produce unsaved changes alert", window().hasAlert());
        });
        waitForReady(page);
    }

    public <T> T retryIfInspectorBug(Supplier<T> doOperation)
    {
        try
        {
            return doOperation.get();
        }
        catch (org.openqa.selenium.WebDriverException ex)
        {
            /*There is a bug in the chrome driver that throws this error, so this will ignore it
             *Bug discussions is found here https://bugs.chromium.org/p/chromedriver/issues/detail?id=2198
             *Bug was supposed to be fixed in 2.38, but there are certain cases where failures will still happen when
             *dealing with iframes
             */
            if (ex.getMessage().contains("unknown error: unhandled inspector error:"))
            {
                return doOperation.get();
            }
            else
            {
                throw ex;
            }
        }
    }

    public void retryIfInspectorBug(Runnable doOperation)
    {
        retryIfInspectorBug(() ->
        {
            doOperation.run();
            return null;
        });
    }
    public <T extends Page> T viaClickToNewWindowFrame(WebElement link, T newPage,String frame)
    {
        return via(() ->
        {
            final Set<String> windowHandles = driver.getWindowHandles();
            LogManager.info("Driver visible window handles: " + windowHandles.toString());
            String baseWindow = windowHandles.stream().filter(d -> !d.equals(driver.getWindowHandle())).collect(Collectors.toCollection(ArrayList::new)).get(0);
            element().click(link);
            waiter().until(d -> driver.getWindowHandles().size() == 1);
            driver.switchTo().window(baseWindow);
            driver.switchTo().defaultContent();
            driver.switchTo().frame(frame);
        }, newPage);
    }
}
