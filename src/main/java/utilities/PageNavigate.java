package utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class PageNavigate extends Navigate
{
    public PageNavigate(Page page)
    {
        super(page);
    }

    /**
     * Some products use pseudo-page-loads where, on "load", element is replaced while
     * the dimmer blocks
     */
    @Override
    protected WebElement getRootElement()
    {
        return driver.findElement(By.cssSelector("body"));
    }
}

