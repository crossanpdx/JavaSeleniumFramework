package utilities;

import com.google.common.collect.Iterables;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Window extends PageUtility
{

    public Window(Page page)
    {
        super(page);
    }

    public void popUpWindow(String title)
    {
        assertNotClosed();
        popUpWindow(title, driver);
    }

    public void tabFocus()
    {
        assertNotClosed();
        tabFocus(driver);
    }

    public boolean hasAlert()
    {
        assertNotClosed();
        return ExpectedConditions.alertIsPresent().apply(driver) != null;
    }

    public void alertConfirm()
    {
        assertNotClosed();
        alertConfirm(driver);
    }

    public void alertDismiss()
    {
        assertNotClosed();
        alertResponse(false, driver);
    }

    public void scrollToTopOfPage()
    {
        assertNotClosed();
        scrollToTopOfPage(driver);
    }

    public void scrollToBottomOfPage()
    {
        assertNotClosed();
        scrollToBottomOfPage(driver);
    }

    public void dismissPopUp()
    {
        assertNotClosed();
        if (driver.findElements((By.cssSelector(" "))).size() != 0)
        {
            driver.findElement(By.cssSelector(" ")).click();
        }
    }

    /**
     * For debugging clicking issues
     * Display a mouse pointer that follows where the mouse is until a real page load occurs
     * To always see the mouse, call this method in Page.assertLoaded
     */
    public void displayMousePointer()
    {
        //code based on https://stackoverflow.com/a/48293356/
        //image based on https://png.icons8.com/windows/50/000000/cursor.png (free with attribution)
        driver.executeScript("if ($('#_mouseImg').length === 0) {"
            + "mousePointer = $('<img id=\"_mouseImg\" style=\"position: absolute; z-index: 999999999; pointer-events: none\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAApCAYAAAAmukmKAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAA7DAAAOwwHHb6hkAAAAB3RJTUUH4gQQEiMV/hcF4gAAAdlJREFUWMO918lqFEEYAOBPERcEFzSu0eBNQd/AoyIefAAVBC+ChxwFQSMYt0QNaMghIPgI3r0K3gTXCLkoQjy54xZBiZdqKIoupqdnagqaYX5q6pu/uurvajhnwO0rJgYNLuH2oMElTA0aHEimKVgcrQOLojH4IUFvlQYP43WC3iwJHsDW0mgKCuhcKbQOzKGTJUHYUoNOlAQr9FU/0U4gDNWgN0qCFfqyH2hTsEJfJOj1kmBf0G7BCn2eoNdKghX6rA3aFsyhV0uCsLlbtFewQp8m6JWSYA4dLwnCpiZov8A12Ifj+JlDV7QY+Aj2Yg9GsAu7w4rNtbHwealphiPRn5vNHLqaXGNNwIPhcHUmfB/GYmbARczjIe7hAk6GMYaxvBN4Khp8AatD/G70mz84hO29lLZloSCnGYxGx494YZzvpXivxYPMlMVZTkbxj1jXBjyKJwnyJrlno9Ge+xbFL7YBfyTYo1BBZjJZjkfxz9jQy7vFfawMfXbid02W6/Epil9uA/7LvIZPR33eh40/E1ZpFf/SJMsY/I5jmX478Cvq+7dmVuawvyn4rkEtvZOZ/nmcqDZ2E/BxOPB2atuSLN/idLc1eRqruuy/gLPRoiraNoZHUav2H7+URi/X3oakAAAAAElFTkSuQmCC\"/>');"
            + "$('body').append(mousePointer);"
            + "$(document).mousemove(function(e){ mousePointer.css('left', e.pageX); mousePointer.css('top', e.pageY); });"
            + "}");
    }

    public static void popUpWindow(String title, RemoteWebDriver driver)
    {
        String mainwindow = driver.getWindowHandle();

        for (String winHandle : driver.getWindowHandles())
        {
            driver.switchTo().window(winHandle);

            if (driver.getTitle().equals(title))
            {
                System.out.println("You are in required window");

                break;
            }
            else
            {
                System.out.println("Title of the page after - switchingTo: " + driver.getTitle());
            }
        }
        driver.switchTo().window(mainwindow);
    }

    //Switch to the newest window (including old windows, if the newer window has closed)
    public static void tabFocus(RemoteWebDriver driver)
    {
        LogManager.trace("Opening in new window");
        Set<String> windows = driver.getWindowHandles();
        LogManager.debug(windows.size() + " windows detected");
        //Selenium provides a LinkedHashSet that has a consistent iteration order
        driver.switchTo().window(Iterables.getLast(windows));
    }

    public static void alertConfirm(RemoteWebDriver driver)
    {
        alertResponse(true, driver);
    }

    public static void alertResponse(boolean accept, RemoteWebDriver driver)
    {
        Alert alert = driver.switchTo().alert();
        if (accept)
        {
            alert.accept();
        }
        else
        {
            alert.dismiss();
        }
        if (driver.getCapabilities().getBrowserName().equals("firefox"))
        {
            LogManager.info("Waiting, to prevent UnhandledAlertException on navigation after accepting an alert (related to https://github.com/mozilla/geckodriver/issues/1247 )");
            try
            {
                Thread.sleep(500); //driver bug, not possible to detect good state except by attempting to navigate (which is unacceptable)
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void scrollToTopOfPage(RemoteWebDriver driver)
    {
        driver.executeScript("window.scrollBy(0,-1000000)", "");
    }

    public static void scrollToBottomOfPage(RemoteWebDriver driver)
    {
        driver.executeScript("window.scrollBy(0,1000000)", "");
    }

    public static void screenshot(RemoteWebDriver driver)
    {
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File newScreenshot = new File(System.getProperty("user.home") + "/Pictures/Screenshot_of_final_state.png");
        try
        {
            Files.deleteIfExists(newScreenshot.toPath());
        }
        catch (IOException x)
        {
        }

        try
        {
            Files.copy(screenshotFile.toPath(), newScreenshot.toPath());
        }
        catch (IOException x)
        {
        }
        LogManager.info(newScreenshot.getAbsolutePath());
        Assert.assertTrue(false);
    }
}
