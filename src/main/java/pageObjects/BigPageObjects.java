package pageObjects;

import org.openqa.selenium.remote.RemoteWebDriver;
import utilities.Navigate;
import utilities.Page;
import utilities.PageNavigate;

public class BigPageObjects extends Page
{
    protected BigPageObjects(RemoteWebDriver driver)
    {
        super(driver);
    }

    @Override
    protected Navigate constructNavigate()
    {
        return new PageNavigate(this);
    }
}
