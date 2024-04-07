import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class BasicTest extends TestHelper {


    private String username = "Malle Maasikas";
    private String password = "password";

    @Test
    public void titleExistsTest(){
        String expectedTitle = "ST Online Store";
        String actualTitle = driver.getTitle();

        assertEquals(expectedTitle, actualTitle);
    }


    /*
    In class Exercise

    Fill in loginLogoutTest() and login mehtod in TestHelper, so that the test passes correctly.

     */
    @Test
    public void loginLogoutTest(){

        login(username, "password");

        // assert that correct page appeared
        boolean isPresent = isElementPresent(By.linkText("New product"));
        assertTrue("New product element is not present", isPresent);

        logout();
    }

    /*
    In class Exercise

     Write a test case, where you make sure, that one can’t log in with a false password

     */
    @Test
    public void loginFalsePassword() {
        login(username, "wrongPassword");

        // assert that message appeared
        boolean noticePresent = isElementPresent(By.xpath("//*[text()='Invalid user/password combination']"));
        Assert.assertTrue("Invalid user/password notice not present", noticePresent);
    }

}
