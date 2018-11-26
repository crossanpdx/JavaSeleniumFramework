package utilities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page assertions.
 */
public class Assert extends PageUtility
{
    public Assert(Page page)
    {
        super(page);
    }

    public void fullPageAssertText(String text)
    {
        assertNotClosed();
        fullPageAssertText(text, driver);
    }

    public void fullPageAssertDoesNotContainText(String text)
    {
        assertNotClosed();
        fullPageAssertDoesNotContainText(text, driver);
    }

    public void waitForClassCSS(String css)
    {
        assertNotClosed();
        waitForClassCSS(css, driver);
    }

    public void verifyURLContainsString(String stringToVerify)
    {
        assertNotClosed();
        verifyURLContainsString(stringToVerify, driver);
    }

    public static void fullPageAssertText(String text, RemoteWebDriver driver)
    {
        LogManager.trace(">>> Assert \"" + text + "\" is present");
        assertThat(driver.getPageSource(), containsString(text));
    }

    public static void waitUntilFullPageHasText(String text, RemoteWebDriver driver)
    {
        Wait.getWaiter(driver).ignoring(AssertionError.class).until(d ->
        {
            fullPageAssertText(text, driver);
            return true;
        });
    }

    public static void fullPageAssertDoesNotContainText(String text, RemoteWebDriver driver)
    {
        LogManager.info(">>> Assert \"" + text + "\" is not present");
        assertFalse("Page source must not contain \"" + text + "\"", driver.getPageSource().contains(text));
    }

    public static void waitForClassCSS(String css, RemoteWebDriver driver)
    {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(css)));
    }

    public static void isElementVisible(WebElement element)
    {
        LogManager.info(">>> Assert that element is visible: " + LogManager.describeElement(element));
        assertTrue(element.isDisplayed());
    }

    public static void verifyURLContainsString(String stringToVerify, RemoteWebDriver driver)
    {
        stringToVerify = stringToVerify.toLowerCase();
        String currentURL = driver.getCurrentUrl().toLowerCase();
        try
        {
            currentURL = java.net.URLDecoder.decode(currentURL, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        LogManager.debug("URL is: " + currentURL);
        LogManager.info(">>> Assert that URL contains \"" + stringToVerify + "\"");
        assertThat(currentURL, containsString(stringToVerify));
    }

    public static void assertTextBoxContains(String text, WebElement element)
    {
        LogManager.trace("Checking if String " + text + " is present");
        assertEquals(text, element.getAttribute("value"));
    }
}
