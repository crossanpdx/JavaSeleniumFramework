package Tests.launch;

import java.util.function.Supplier;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LocalTestEnvironment
{
    private RemoteWebDriver sharedDriver = null;

    /* If true, reuse the web driver between all tests, except failing tests.
     * This increases the risk of a preceding test causing a following test to fail,
     * but improves performance and prevents the new windows from stealing your focus and interrupting your work.
     * For this to work, each test must correctly log out upon successful completion.
     */
    public boolean willReuseDriver()
    {
        return true;
    }

    private final Supplier<RemoteWebDriver> constructDriver;

    private LocalTestEnvironment(Supplier<RemoteWebDriver> constructDriver)
    {
        this.constructDriver = constructDriver;
    }

    public static LocalTestEnvironment chrome()
    {
        return new LocalTestEnvironment(() ->
        {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("-browserSessionReuse");
            return new ChromeDriver(options);
        });
    }

    public static LocalTestEnvironment firefox()
    {
        return new LocalTestEnvironment(() ->
        {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("-browserSessionReuse");
            return new FirefoxDriver(options);
        });
    }

    public void destroySharedResources()
    {
        if (sharedDriver != null)
        {
            if (sharedDriver.getCapabilities().getBrowserName().equals("firefox"))
            {
                // FirefoxDriver.quit() hangs if this causes an alert
                // https://github.com/mozilla/geckodriver/issues/1151
                try
                {
                    if (ExpectedConditions.alertIsPresent().apply(sharedDriver) == null)
                    {
                        sharedDriver.navigate().to("https://google.com");
                    }
                }
                catch (WebDriverException ex)
                {
                }
            }
            sharedDriver.quit();
            sharedDriver = null;
        }
    }

    private RemoteWebDriver newDriver()
    {
        RemoteWebDriver driver = constructDriver.get();
        driver.manage().window().maximize();
        return driver;
    }

    private RemoteWebDriver getDriver()
    {
        if (willReuseDriver())
        {
            if (sharedDriver == null)
            {
                sharedDriver = newDriver();
            }
            return sharedDriver;
        }
        else
        {
            return newDriver();
        }
    }
}
