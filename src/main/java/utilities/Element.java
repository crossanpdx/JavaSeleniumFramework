package utilities;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Point;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Element extends PageUtility
{
    public Element(Page page)
    {
        super(page);
    }

    public WebElement foundByCSS(String css)
    {
        assertNotClosed();
        return foundByCSS(css, driver);
    }

    public WebElement foundByID(String id)
    {
        assertNotClosed();
        return foundByID(id, driver);
    }

    public WebElement foundByLinkText(String linktext)
    {
        assertNotClosed();
        return foundByLinkText(linktext, driver);
    }

    public WebElement foundByXPath(String xpath)
    {
        assertNotClosed();
        return foundByXPath(xpath, driver);
    }

    public WebElement foundByClassName(String classname)
    {
        assertNotClosed();
        return foundByClassName(classname, driver);
    }

    public WebElement foundByPartialLinkText(String linkText)
    {
        assertNotClosed();
        return foundByPartialLinkText(linkText, driver);
    }

    public WebElement foundByPartialLinkText(String linkText, SearchContext context)
    {
        return Wait.waiter(By.partialLinkText(linkText), context);
    }

    public WebElement foundByName(String name)
    {
        assertNotClosed();
        return foundByName(name, driver);
    }

    public void click(WebElement element)
    {
        click(element, driver);
    }

    public void click(By by, SearchContext context)
    {
        click(by, context, driver);
    }

    public void click(By by)
    {
        click(by, driver);
    }

    public boolean isInView(WebElement element)
    {
        return isInView(element, driver);
    }

    /**
     * Given <div>foo<span>bar</span>oof</div>, return "foooof"
     */
    public String getOwnText(WebElement element)
    {
        //with thanks to https://stackoverflow.com/a/45708385/1914005
        return (String) driver.executeScript("return $(arguments[0]).contents().filter(function () { return this.nodeType === Node.TEXT_NODE; }).text();", element);
    }

    /**
     * Get visible and invisible text of an element
     * (WebElement.getText() only returns visible text)
     */
    public static String getAllText(WebElement element)
    {
        return element.getAttribute("textContent");
    }

    public static WebElement foundByCSS(String css, SearchContext context)
    {
        return Wait.waiter(By.cssSelector(css), context);
    }

    public static WebElement foundByID(String id, SearchContext context)
    {
        return Wait.waiter(By.id(id), context);
    }

    public static WebElement foundByLinkText(String linktext, SearchContext context)
    {
        return Wait.waiter(By.linkText(linktext), context);
    }

    public static WebElement foundByXPath(String xpath, SearchContext context)
    {
        return Wait.waiter(By.xpath(xpath), context);
    }

    public static WebElement foundByClassName(String classname, SearchContext context)
    {
        return Wait.waiter(By.className(classname), context);
    }

    public static WebElement foundByName(String name, SearchContext context)
    {
        return Wait.waiter(By.name(name), context);
    }

    /**
     * Click an element, but only once it is really clickable. Helpful to eliminate waits.
     * Really tries as hard as it can to click the element, handling a number of common errors.
     * We should refactor this method to split it into smaller methods.
     */
    public static void click(WebElement element, RemoteWebDriver driver)
    {
        Objects.requireNonNull(element, "Element must not be null");
        ExpectedCondition<Boolean> isClickable = ExpectedConditions.and(ExpectedConditions.elementToBeClickable(element), d -> positionedOnPage(element));
        if (!isClickable.apply(driver))
        {
            LogManager.debug("Waiting for element to be clickable: " + LogManager.describeElement(element));
            Wait.getWaiter(driver).until(isClickable);
        }
        //elementToBeClickable only checks that it's visible, now wait for nothing to be in the way
        //sadly there's no easier way to check than just trying to click
        String elementDescription = LogManager.describeElement(element);
        try
        {
            Wait.getWaiter(driver).withMessage("to click element " + elementDescription).until(new Function<RemoteWebDriver, Boolean>()
            {
                boolean loggedAndScrolled = false;
                boolean loggedUseActions = false;

                @Override
                public Boolean apply(RemoteWebDriver driver1)
                {
                    try
                    {
                        try
                        {
                            element.click();
                        }
                        catch (ElementNotInteractableException ex)
                        {
                            if (ex.getMessage().split("\n")[0].contains("could not be scrolled into view") && driver.getCapabilities().getBrowserName().equals("firefox"))
                            {
                                if (!loggedUseActions)
                                {
                                    LogManager.debug("Detected could not be scrolled into view bug ( https://github.com/mozilla/geckodriver/issues/1007 )");
                                    LogManager.debug("> Try clicking the element's location instead");
                                    loggedUseActions = true;
                                }
                                new Actions(driver).moveToElement(element).click().perform();
                            }
                            else
                            {
                                throw ex;
                            }
                        }
                        return true;
                    }
                    catch (WebDriverException ex)
                    {
                        //too bad they don't have a more specific NotClickableException
                        //this code is fragile because it depends on the message, but it's easy to fix
                        String firstLine = ex.getMessage().split("\n", 2)[0];
                        Matcher obscuringElement = Pattern.compile("Other element would receive the click: (<[^>]*>)|because another element (<[^>]*>) obscures it").matcher(firstLine);
                        if ((ex instanceof ElementClickInterceptedException || firstLine.contains("is not clickable")) && obscuringElement.find())
                        {
                            if (!loggedAndScrolled)
                            {
                                LogManager.info("Waiting for other element to get out of the way of clicking: " + obscuringElement.group(1));
                                LogManager.info("> Scroll to top of page (in case the other element is the top bar)");
                                Window.scrollToTopOfPage(driver);
                                loggedAndScrolled = true;
                            }
                            return false;
                        }
                        else
                        {
                            //we don't know what kind of WebDriverException this is, fail
                            throw ex;
                        }
                    }
                }
            });
        }
        catch (TimeoutException ex)
        {
            //clicking is impossible; call click one last time to get the error it causes
            element.click();
        }
    }

    public static void click(By by, SearchContext context, RemoteWebDriver driver)
    {
        click(Wait.waiter(by, context), driver);
    }

    public static void click(By by, RemoteWebDriver driver)
    {
        click(by, driver, driver);
    }

    /**
     * @return true if the element is not off the top or left sides of the screen (but you might still need to scroll to see it)
     */
    public static boolean positionedOnPage(WebElement element)
    {
        Point location = element.getLocation();
        return location.x >= 0 && location.y >= 0;
    }

    //Checks to see if element is in view
    //Uses javascript to see if the element is in the window so it can be clicked.
    public static boolean isInView(WebElement element, RemoteWebDriver driver)
    {
        return (boolean) driver.executeScript("var rect = arguments[0].getBoundingClientRect(); return (rect.top >= 0 && rect.left >= 0 && rect.bottom <=  $(window).height() && rect.right <=$(window).width())", element);
    }
}