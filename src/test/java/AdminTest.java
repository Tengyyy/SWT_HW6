import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class AdminTest extends TestHelper {

    @Test
    public void titleExistsTest() {
        String expectedTitle = "ST Online Store";
        String actualTitle = driver.getTitle();

        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    public void registerTest() {
        register(USERNAME, PASSWORD);

        // assert that correct page appeared
        boolean isPresent = isElementPresent(By.xpath(String.format("//*[text()='User %s was successfully created.']", USERNAME)));
        assertTrue("'User created' notice is not present", isPresent);

        deleteUser();
    }

    @Test
    public void registerExistingUsername() {
        register(USERNAME, PASSWORD);
        logout();

        // Try to register with the same username
        register(USERNAME, PASSWORD);

        // assert that message appeared
        boolean noticePresent = isElementPresent(By.xpath("//*[text()='Name has already been taken']"));
        assertTrue("'Name has already been taken' error not present", noticePresent);

        login(USERNAME, PASSWORD);
        deleteUser();
    }

    @Test
    public void registerPasswordMismatch() {
        register(USERNAME, PASSWORD, "differentPassword");

        // assert that message appeared
        boolean noticePresent = isElementPresent(By.xpath("//*[text()=\"Password confirmation doesn't match Password\"]"));
        assertTrue("'Password confirmation doesn't match Password' error not present", noticePresent);
    }

    @Test
    public void registerEmptyPassword() {
        register(USERNAME, "");

        // assert that message appeared
        boolean noticePresent = isElementPresent(By.xpath("//*[text()=\"Password can't be blank\"]"));
        assertTrue("'Password can't be blank' error not present", noticePresent);
    }

    @Test
    public void loginLogoutTest() {
        register(USERNAME, PASSWORD);
        logout();

        login(USERNAME, PASSWORD);
        // assert that correct page appeared
        boolean isPresent = isElementPresent(By.linkText("New product"));
        assertTrue("New product element is not present", isPresent);
        logout();

        // assert that we are logged out now
        boolean isLoginPresent = isElementPresent(By.linkText("Login"));
        assertTrue("Login button not present", isLoginPresent);

        login(USERNAME, PASSWORD);
        deleteUser();
    }

    @Test
    public void loginFalsePassword() {
        register(USERNAME, PASSWORD);
        logout();

        login(USERNAME, "wrongPassword");

        // assert that message appeared
        boolean noticePresent = isElementPresent(By.xpath("//*[text()='Invalid user/password combination']"));
        assertTrue("'Invalid user/password' notice not present", noticePresent);

        login(USERNAME, PASSWORD);
        deleteUser();
    }

    @Test
    public void editUsernameTest() {
        register(USERNAME, PASSWORD);

        goToPage("Admin");

        // Edit user info
        WebElement user = driver.findElement(By.id(USERNAME));
        user.findElement(By.linkText("Edit")).click();

        String newUsername = "Uku Suviste";
        driver.findElement(By.id("user_name")).clear();
        driver.findElement(By.id("user_name")).sendKeys(newUsername);


        By updateButtonXpath = By.xpath("//input[@value='Update User']");
        // click on the button
        driver.findElement(updateButtonXpath).click();

        boolean noticePresent = isElementPresent(By.xpath(String.format("//*[text()='User %s was successfully updated.']", newUsername)));
        assertTrue("'User successfully updated' notice not present", noticePresent);

        logout();

        //Try to login with the new username
        login(newUsername, PASSWORD);

        // assert that correct page appeared
        boolean isPresent = isElementPresent(By.linkText("New product"));
        assertTrue("New product element is not present", isPresent);

        deleteUser();
    }

    @Test
    public void editPasswordTest() {

        register(USERNAME, PASSWORD);

        goToPage("Admin");

        // Edit user info
        WebElement user = driver.findElement(By.id(USERNAME));
        user.findElement(By.linkText("Edit")).click();

        String newPassword = "newPassword";
        driver.findElement(By.id("user_password")).sendKeys(newPassword);
        driver.findElement(By.id("user_password_confirmation")).sendKeys(newPassword);


        By updateButtonXpath = By.xpath("//input[@value='Update User']");
        // click on the button
        driver.findElement(updateButtonXpath).click();

        boolean noticePresent = isElementPresent(By.xpath(String.format("//*[text()='User %s was successfully updated.']", USERNAME)));
        assertTrue("'User successfully updated' notice not present", noticePresent);

        logout();

        //Try to log in with the new password
        login(USERNAME, newPassword);

        // assert that correct page appeared
        boolean isPresent = isElementPresent(By.linkText("New product"));
        assertTrue("New product element is not present", isPresent);

        deleteUser();
    }

    @Test
    public void createAndDeleteProductTest() {
        register(USERNAME, PASSWORD);

        WebElement tbody = driver.findElement(By.className("products_column"))
                .findElement(By.tagName("table"))
                .findElement(By.tagName("tbody"));

        int initialNumOfProducts = tbody.findElements(By.tagName("tr")).size();

        String productTitle = "Test product";
        String productDescription = "Description123";
        String productType = "Other";
        String productPrice = "10.00";

        //Create new product
        driver.findElement(By.linkText("New product")).click();
        driver.findElement(By.id("product_title")).sendKeys(productTitle);
        driver.findElement(By.id("product_description")).sendKeys(productDescription);

        Select productTypeSelect = new Select(driver.findElement(By.id("product_prod_type")));
        productTypeSelect.selectByValue(productType);

        driver.findElement(By.id("product_price")).sendKeys(productPrice);

        By createProductButtonXpath = By.xpath("//input[@value='Create Product']");
        // click on the button
        driver.findElement(createProductButtonXpath).click();

        //Check that new item was added
        tbody = driver.findElement(By.className("products_column"))
                .findElement(By.tagName("table"))
                .findElement(By.tagName("tbody"));
        List<WebElement>products = tbody.findElements(By.tagName("tr"));
        assertEquals("New number of products is incorrect", initialNumOfProducts + 1, products.size());

        WebElement newProduct = driver.findElement(By.id(productTitle));
        WebElement titleElement = newProduct.findElement(By.className("list_description")).findElement(By.tagName("a"));
        assertEquals("Incorrect product title", productTitle, titleElement.getText());

        WebElement categoryElement = newProduct.findElement(By.className("list_description")).findElement(By.className("prod_categ"));
        assertEquals("Incorrect product category", "Other", categoryElement.getText());

        titleElement.click();

        List<WebElement> elements = driver.findElement(By.className("products_column")).findElements(By.tagName("p"));

        assertEquals("Incorrect product title", "Title: " + productTitle, elements.get(1).getText());
        assertEquals("Incorrect product description", "Description: " + productDescription, elements.get(2).getText());
        assertEquals("Incorrect product type", "Type: " + productType, elements.get(3).getText());
        assertEquals("Incorrect product price", "Price: €" + productPrice, elements.get(4).getText());


        driver.findElement(By.linkText("Back")).click();

        //Delete new product and check that number of products decreased
        newProduct = driver.findElement(By.id(productTitle));
        newProduct.findElement(By.linkText("Delete")).click();

        tbody = driver.findElement(By.className("products_column"))
                .findElement(By.tagName("table"))
                .findElement(By.tagName("tbody"));
        products = tbody.findElements(By.tagName("tr"));

        assertEquals("Wrong number of products after deletion", initialNumOfProducts, products.size());

        deleteUser();
    }

    @Test
    public void editProductTest() {
        register("username2", PASSWORD); // This test will fail so using different username because this user won't be deleted

        String initialTitle = "Initial product title";

        //Create new product
        driver.findElement(By.linkText("New product")).click();
        driver.findElement(By.id("product_title")).sendKeys(initialTitle);
        driver.findElement(By.id("product_description")).sendKeys("Initial description");

        Select productTypeSelect = new Select(driver.findElement(By.id("product_prod_type")));
        productTypeSelect.selectByValue("Books");

        driver.findElement(By.id("product_price")).sendKeys("10.00");

        By createProductButtonXpath = By.xpath("//input[@value='Create Product']");
        // click on the button
        driver.findElement(createProductButtonXpath).click();

        WebElement newProduct = driver.findElement(By.id(initialTitle));
        newProduct.findElement(By.linkText("Edit")).click();

        String newTitle = "New title";
        String newDescription = "New description";
        String newType = "Sunglasses";
        String newPrice = "999.99";

        driver.findElement(By.id("product_title")).clear();
        driver.findElement(By.id("product_title")).sendKeys(newTitle);

        driver.findElement(By.id("product_description")).clear();
        driver.findElement(By.id("product_description")).sendKeys(newDescription);

        productTypeSelect = new Select(driver.findElement(By.id("product_prod_type")));
        productTypeSelect.selectByValue(newType);

        driver.findElement(By.id("product_price")).clear();
        driver.findElement(By.id("product_price")).sendKeys(newPrice);

        By updateProductButtonXpath = By.xpath("//input[@value='Update Product']");
        // click on the button
        driver.findElement(updateProductButtonXpath).click();

        boolean noticePresent = isElementPresent(By.xpath("//*[text()='Product was successfully updated.']"));
        assertTrue("'Product updated' notice not present", noticePresent);

        List<WebElement> elements = driver.findElement(By.className("products_column")).findElements(By.tagName("p"));

        assertEquals("Incorrect product title", "Title: " + newTitle, elements.get(1).getText());
        assertEquals("Incorrect product description", "Description: " + newDescription, elements.get(2).getText());
        assertEquals("Incorrect product type", "Type: " + newType, elements.get(3).getText());
        assertEquals("Incorrect product price", "Price: €" + newPrice, elements.get(4).getText());


        driver.findElement(By.linkText("Back")).click();

        //Delete new product and check that number of products decreased
        newProduct = driver.findElement(By.id(newTitle));
        newProduct.findElement(By.linkText("Delete")).click();

        deleteUser();
    }

    @Test
    public void negativePriceProductTest() {
        register(USERNAME, PASSWORD);

        //Create new product
        driver.findElement(By.linkText("New product")).click();
        driver.findElement(By.id("product_title")).sendKeys("Title");
        driver.findElement(By.id("product_description")).sendKeys("Description");

        Select productTypeSelect = new Select(driver.findElement(By.id("product_prod_type")));
        productTypeSelect.selectByValue("Books");

        driver.findElement(By.id("product_price")).sendKeys("-100");

        By createProductButtonXpath = By.xpath("//input[@value='Create Product']");
        // click on the button
        driver.findElement(createProductButtonXpath).click();

        boolean noticePresent = isElementPresent(By.xpath("//*[text()='Price must be greater than or equal to 0.01']"));
        assertTrue("'Price greater than' error not present", noticePresent);

        deleteUser();
    }

}
