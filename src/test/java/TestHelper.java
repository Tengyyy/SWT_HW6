import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TestHelper {

    static WebDriver driver;
    final int waitForResposeTime = 4;

    // here write a link to your admin website (e.g. http://my-app.herokuapp.com/admin)

    // here write a link to your website (e.g. http://my-app.herokuapp.com/)
    String baseUrl = "http://127.0.0.1:3000";
    String baseUrlAdmin = baseUrl + "/admin";

    static final String USERNAME = "username";
    static final String PASSWORD = "password";

    @Before
    public void setUp() {

        // if you use Firefox:
        System.setProperty("webdriver.gecko.driver", "C:\\Users\\hansu\\OneDrive\\Desktop\\geckodriver-v0.30.0-win64\\geckodriver.exe");
        driver = new FirefoxDriver();

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(baseUrl);

    }

    void goToPage(String page) {
        WebElement elem = driver.findElement(By.linkText(page));
        elem.click();
        waitForElementById(page);
    }

    void waitForElementById(String id) {
        new WebDriverWait(driver, waitForResposeTime).until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
    }

    public boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    void register(String username, String password) {
        register(username, password, password);
    }

    void register(String username, String password, String passwordConfirmation) {
        driver.get(baseUrlAdmin);

        driver.findElement(By.linkText("Register")).click();

        driver.findElement(By.id("user_name")).sendKeys(username);
        driver.findElement(By.id("user_password")).sendKeys(password);
        driver.findElement(By.id("user_password_confirmation")).sendKeys(passwordConfirmation);

        By registerButtonXpath = By.xpath("//input[@value='Create User']");
        // click on the button
        driver.findElement(registerButtonXpath).click();
    }

    void login(String username, String password) {

        driver.get(baseUrlAdmin);

        driver.findElement(By.linkText("Login")).click();

        driver.findElement(By.id("name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);

        By loginButtonXpath = By.xpath("//input[@value='Login']");
        // click on the button
        driver.findElement(loginButtonXpath).click();
    }

    void logout() {
        WebElement logout = driver.findElement(By.linkText("Logout"));
        logout.click();

        waitForElementById("Admin");
    }

    void deleteUser() {
        goToPage("Admin");
        driver.findElement(By.linkText("Delete")).click();
    }

    @After
    public void tearDown() {
        driver.close();
    }

}