package Tests.SmokeTests;

import Tests.launch.BaseTest;
import org.junit.Test;
import pageObjects.HomePageObjects;


public class healthCheckTests extends BaseTest
{
    public HomePageObjects declareHomePage()
    {
        return new HomePageObjects(driver);
    }

    @Test
    public void landingPageCheck()
    {
        declareHomePage().landingPageLink();
    }

    @Test
    public void bigPageCheck()
    {
        declareHomePage().bigPageLink();
    }

    @Test
    public void pricingPageCheck()
    {
        declareHomePage().pricingPageLink();
    }

    @Test
    public void loginPageCheck()
    {
        declareHomePage().loginPageLink();
    }
}
