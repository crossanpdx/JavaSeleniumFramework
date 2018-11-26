package Tests.SmokeTests;

import Tests.launch.BaseTest;
import org.junit.Test;
import pageObjects.HomePageObjects;
import pageObjects.LoginPageObjects;

public class LoginPageTests extends BaseTest
{
    /*This is an example of automating to create a new user.
    This test skips selecting the captcha verification
     */
    @Test
    public void createNewUser()
    {
        HomePageObjects homePageObjects = new HomePageObjects(driver);
        LoginPageObjects loginPageObjects = homePageObjects.loginPageLink();
        loginPageObjects.createAccountLink();
        loginPageObjects.enterFirstName();
        loginPageObjects.enterLastName();
        loginPageObjects.enterEmail();
        loginPageObjects.enterPassword();
        loginPageObjects.acceptUserAgreement();
        loginPageObjects.signUpBtn();
    }
}
