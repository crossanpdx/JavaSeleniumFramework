package pageObjects;

import org.openqa.selenium.remote.RemoteWebDriver;
import utilities.Navigate;
import utilities.Page;
import utilities.PageNavigate;

public class PricingPageObjects extends Page
{
    protected PricingPageObjects(RemoteWebDriver driver)
    {
        super(driver);
    }

    @Override
    protected Navigate constructNavigate()
    {
        return new PageNavigate(this);
    }
}
