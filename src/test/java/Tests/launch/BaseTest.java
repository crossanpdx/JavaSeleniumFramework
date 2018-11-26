package Tests.launch;

import Tests.Reporting.MyRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import utilities.LogManager;

@RunWith(MyRunner.class)
public class BaseTest
{
    private static final LocalTestEnvironment testEnvironment;

    static
    {
        LogManager.info("Detected local (dev machine) test environment");
        testEnvironment = LocalTestEnvironment.chrome();
    }

    protected RemoteWebDriver driver;

    private static LocalTestEnvironment getTestEnvironment()
    {
        return testEnvironment;
    }

    public void destroySharedResources()
    {
        destroySharedDriver();
    }

    @Before
    public void setUp()
    {
        driver = new ChromeDriver();
        LogManager.info("### Test Begins ###");
        driver.get("https://www.ultimateqa.com/automation/");
        LogManager.info(">>> Launching browser and navigating to starting URL");
    }

    @After
    public void tearDown()
    {
        driver.quit();
    }

    @AfterClass
    public static void destroySharedDriver()
    {
        getTestEnvironment().destroySharedResources();
    }
}