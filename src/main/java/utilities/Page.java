package utilities;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * A Page is somewhere the browser might navigate to.
 * Typically, if the browser URL changed, you've moved to a new Page
 * (though if it looks the same just with different data, it's probably a new instance of the same class of Page).
 * Page provides PageUtility fields to its subclasses to help perform actions.
 * Page provides a few public methods for actions that can be taken on any page.
 * Page subclasses provide public instance methods for each action you can take while on that page.
 *
 * All Page subclasses and the objects their public methods return should be Encapsulated Objects
 * that are safe for tests to call any public methods of, and will maintain a consistent ready state in the page.
 * Follow all rules for Encapsulated Objects.
 *
 * Page subclass constructors SHOULD NOT do anything other than assign instance variables
 * --new Page instances are constructed before you actually navigate to the page.
 */
public abstract class Page
{
    protected final RemoteWebDriver driver;
    private boolean closed = false;
    //exposed via package-private to PageUtility
    private final Assert assrt;
    private final Element element;
    private final Navigate navigate;
    private final Wait waiter;
    private final Window window;

    protected Page(RemoteWebDriver driver)
    {
        this.driver = driver;
        assrt = new Assert(this);
        element = constructElement();
        waiter = constructWait();
        window = new Window(this);
        navigate = constructNavigate();
    }

    /**
     * Return a Navigate subclass appropriate for the product this page is in.
     * See {@link Navigate} documentation.
     */
    protected abstract Navigate constructNavigate();

    protected void assertNotClosed()
    {
        if (closed)
        {
            throw new IllegalStateException("Can't use page after it was closed");
        }
    }

    protected void close()
    {
        assertNotClosed();
        closed = true;
    }

    protected Element constructElement(){
        return new Element(this);
    }

    protected Wait constructWait()
    {
        return new Wait(this);
    }

    /**
     * Check that you're really on the page you think you're on.
     */
    protected void assertLoaded()
    {
        assertTrue("Page must be ready", Wait.pageFinishedLoading.apply(driver));
        if (driver.findElements(By.id("main-frame-error")).size() != 0)
        {
            fail("Reached chrome error page: " + driver.findElement(By.tagName("body")).getText());
        }
    }

    // Trouble: Some pageObjects' other methods might not behave expectedly after a refresh
    // They can override refresh to disable it if needed
    public void refresh()
    {
        refresh(false);
    }

    public void refresh(boolean acceptAlert)
    {
        navigate().refresh(acceptAlert);
    }

    protected Assert assrt()
    {
        return assrt;
    }

    protected Element element()
    {
        return element;
    }

    protected Navigate navigate()
    {
        return navigate;
    }

    protected Wait waiter()
    {
        return waiter;
    }

    protected Window window()
    {
        return window;
    }

}
