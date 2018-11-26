package pageObjects;

import java.util.Random;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import utilities.Navigate;
import utilities.Page;
import utilities.PageNavigate;

public class LoginPageObjects extends Page
{
    protected LoginPageObjects(RemoteWebDriver driver)
    {
        super(driver);
    }

    @Override
    protected Navigate constructNavigate()
    {
        return new PageNavigate(this);
    }

    Random randomGenerator = new Random();

    public void enterEmail()
    {
        Random randomGenerator = new Random();
        String emailId = randomGenerator + "@gmail.com";

        driver.findElement(By.cssSelector("#user_email")).sendKeys(emailId);
    }

    public void enterPassword()
    {
        String passId = "a" + randomGenerator;

        driver.findElement(By.cssSelector("#user_password")).sendKeys(passId);
    }

    public void enterFirstName()
    {
        String fNameId = "Tester" + randomGenerator;

        driver.findElement(By.cssSelector("#user_first_name")).sendKeys(fNameId);
    }

    public void enterLastName()
    {
        String lNameId = "Z" + randomGenerator;
        driver.findElement(By.cssSelector("#user_last_name")).sendKeys(lNameId);
    }

    public void createAccountLink()
    {
        driver.findElement(By.cssSelector("#content-push > div > div > div.user-sign-up__bottom > div > div > a")).click();
    }

    public void acceptUserAgreement()
    {
        driver.findElement(By.cssSelector("#user_terms")).click();
    }

    public void signUpBtn()
    {
        driver.findElement(By.cssSelector("#btn-signup")).click();
    }

}
