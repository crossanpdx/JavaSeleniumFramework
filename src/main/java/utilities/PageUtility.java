package utilities;

import static org.junit.Assert.assertNotNull;

import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Superclass that provides access to low-level Page functions
 * Most Page Interface and Selenium Implementation classes that don't extend Page extend this class
 * to gain access to the page's internals.
 * Subclasses MUST call assertNotClosed() on most public methods to ensure that the test has not already navigated away
 * Subclasses MUST NOT do anything in their constructor other than assign instance variables
 */
public class PageUtility
{
    protected final Page page;
    protected final RemoteWebDriver driver;

    protected PageUtility(Page page)
    {
        this.page = page;
        this.driver = page.driver;
    }

    //expose Page methods to utilities
    protected void assertNotClosed()
    {
        page.assertNotClosed();
    }

    protected void close()
    {
        page.close();
    }

    protected void waitForReady(Page newPage)
    {
        assertNotNull(newPage);
        newPage.navigate().waitForReady();
        newPage.assertLoaded();
    }

    //expose PageUtilities to each other
    //use package-private access to these fields
    protected Assert assrt()
    {
        return page.assrt();
    }

    protected Element element()
    {
        return page.element();
    }

    protected Navigate navigate()
    {
        return page.navigate();
    }

    protected Wait waiter()
    {
        return page.waiter();
    }

    protected Window window()
    {
        return page.window();
    }
}
