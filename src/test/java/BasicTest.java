import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class BasicTest extends TestHelper {

    @Test
    public void titleExistsTest() {
        String expectedTitle = "ST Online Store";
        String actualTitle = driver.getTitle();

        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    public void registerTest() {
        String username = generateRandomString();
        register(username, PASSWORD);

        // assert that correct page appeared
        boolean isPresent = isElementPresent(By.xpath(String.format("//*[text()='User %s was successfully created.']", username)));
        assertTrue("'User created' notice is not present", isPresent);

        logout();
    }

    @Test
    public void registerExistingUsername() {
        String username = generateUserAndRegister();

        // Try to register with the same username
        register(username, PASSWORD);

        // assert that message appeared
        boolean noticePresent = isElementPresent(By.xpath("//*[text()='Name has already been taken']"));
        Assert.assertTrue("'Name has already been taken' error not present", noticePresent);
    }

    @Test
    public void registerPasswordMismatch() {

        String username = generateRandomString();
        register(username, PASSWORD, "differentPassword");

        // assert that message appeared
        boolean noticePresent = isElementPresent(By.xpath("//*[text()=\"Password confirmation doesn't match Password\"]"));
        Assert.assertTrue("'Password confirmation doesn't match Password' error not present", noticePresent);
    }

    @Test
    public void loginLogoutTest() {

        String username = generateUserAndRegister();

        // Try to log in with the same user
        login(username, PASSWORD);

        // assert that correct page appeared
        boolean isPresent = isElementPresent(By.linkText("New product"));
        assertTrue("New product element is not present", isPresent);

        logout();
    }

    @Test
    public void loginFalsePassword() {
        String username = generateUserAndRegister();

        login(username, "wrongPassword");

        // assert that message appeared
        boolean noticePresent = isElementPresent(By.xpath("//*[text()='Invalid user/password combination']"));
        Assert.assertTrue("'Invalid user/password' notice not present", noticePresent);
    }

}
