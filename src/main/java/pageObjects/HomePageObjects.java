package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import utilities.Navigate;
import utilities.Page;
import utilities.PageNavigate;

public class HomePageObjects extends Page
{

    public HomePageObjects(RemoteWebDriver driver)
    {
        super(driver);
    }

    @Override
    protected Navigate constructNavigate()
    {
        return new PageNavigate(this);
    }

    public LandingPageObjects landingPageLink()
    {
        //waiter().until(ExpectedConditions.visibilityOf(element().foundByCSS("#et-boc > div > div.et_pb_section.et_pb_section_1.et_section_specialty > div > div.et_pb_column.et_pb_column_3_4.et_pb_column_0.et_pb_specialty_column.et_pb_css_mix_blend_mode_passthrough > div > div > div > div > ul > li:nth-child(2) > a")));
        WebElement landingPage = driver.findElement(By.cssSelector("#et-boc > div > div.et_pb_section.et_pb_section_1.et_section_specialty > div > div.et_pb_column.et_pb_column_3_4.et_pb_column_0.et_pb_specialty_column.et_pb_css_mix_blend_mode_passthrough > div > div > div > div > ul > li:nth-child(2) > a"));
        return navigate().viaClick(landingPage, new LandingPageObjects(driver));
    }

    public BigPageObjects bigPageLink()
    {
       // waiter().until(ExpectedConditions.visibilityOf(element().foundByCSS("#et-boc > div > div.et_pb_section.et_pb_section_1.et_section_specialty > div > div.et_pb_column.et_pb_column_3_4.et_pb_column_0.et_pb_specialty_column.et_pb_css_mix_blend_mode_passthrough > div > div > div > div > ul > li:nth-child(2) > a")));
        WebElement bigPage = driver.findElement(By.cssSelector("#et-boc > div > div.et_pb_section.et_pb_section_1.et_section_specialty > div > div.et_pb_column.et_pb_column_3_4.et_pb_column_0.et_pb_specialty_column.et_pb_css_mix_blend_mode_passthrough > div > div > div > div > ul > li:nth-child(1) > a"));
        return navigate().viaClick(bigPage, new BigPageObjects(driver));
    }

    public PricingPageObjects pricingPageLink()
    {
       // waiter().until(ExpectedConditions.visibilityOf(element().foundByCSS("#et-boc > div > div.et_pb_section.et_pb_section_1.et_section_specialty > div > div.et_pb_column.et_pb_column_3_4.et_pb_column_0.et_pb_specialty_column.et_pb_css_mix_blend_mode_passthrough > div > div > div > div > ul > li:nth-child(2) > a")));
        WebElement pricingPage = driver.findElement(By.cssSelector("#et-boc > div > div.et_pb_section.et_pb_section_1.et_section_specialty > div > div.et_pb_column.et_pb_column_3_4.et_pb_column_0.et_pb_specialty_column.et_pb_css_mix_blend_mode_passthrough > div > div > div > div > ul > li:nth-child(3) > a"));
        return navigate().viaClick(pricingPage, new PricingPageObjects(driver));
    }

    public LoginPageObjects loginPageLink()
    {
       // waiter().until(ExpectedConditions.visibilityOf(element().foundByCSS("#et-boc > div > div.et_pb_section.et_pb_section_1.et_section_specialty > div > div.et_pb_column.et_pb_column_3_4.et_pb_column_0.et_pb_specialty_column.et_pb_css_mix_blend_mode_passthrough > div > div > div > div > ul > li:nth-child(2) > a")));
        WebElement loginPage = driver.findElement(By.cssSelector("#et-boc > div > div.et_pb_section.et_pb_section_1.et_section_specialty > div > div.et_pb_column.et_pb_column_3_4.et_pb_column_0.et_pb_specialty_column.et_pb_css_mix_blend_mode_passthrough > div > div > div > div > ul > li:nth-child(6) > a"));
        return navigate().viaClick(loginPage, new LoginPageObjects(driver));
    }
}
