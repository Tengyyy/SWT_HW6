import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotEquals;

public class EndUserTest extends TestHelper{
    @Test
    public void addProductTest() {
        driver.findElement(By.className("button_to")).click();

        // Check if the element div with class "cart" appeared
        boolean isCartPresent = isElementPresent(By.xpath("/html/body/div[4]/div[2]/div[3]/div[2]/form/input[1]"));

        assertTrue("Cart should be present after clicking 'Add Product'", isCartPresent);
    }

    @Test
    public void increaseAndDecreaseQuantityTest() {
        // Add to cart and check quantity
        driver.findElement(By.className("button_to")).click();
        String quantity = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div/table/tbody/tr[1]/td[1]")).getText();
        assertEquals("One item in cart", "1×", quantity);

        // Increase quantity and check
        driver.findElement(By.xpath("/html/body/div[4]/div[1]/div/table/tbody/tr[1]/td[5]/a")).click();
        String newQuantity = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div/table/tbody/tr[1]/td[1]")).getText();
        assertEquals("Two items in cart", "2×", newQuantity);

        // Decrease quantity and check
        driver.findElement(By.xpath("/html/body/div[4]/div[1]/div/table/tbody/tr[1]/td[4]/a")).click();
        String decreasedQuantity = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div/table/tbody/tr[1]/td[1]")).getText();
        assertEquals("One item in cart again", "1×", decreasedQuantity);
    }

    @Test
    public void removeItemTest() {
        // Add an item to the cart
        driver.findElement(By.xpath("/html/body/div[4]/div[2]/div[3]/div[2]/form/input[1]")).click();

        boolean isItemPresent = isElementPresent(By.xpath("/html/body/div[4]/div[1]/div/table/tbody/tr[1]/td[2]"));
        Assert.assertTrue(isItemPresent);

        // Check delete button present; click it
        Assert.assertTrue(isElementPresent(By.linkText("X")));
        driver.findElement(By.linkText("X")).click();

        boolean isNoticePresent = isElementPresent(By.xpath("//*[@id=\"notice\"]"));
        Assert.assertTrue(isNoticePresent);
    }

    @Test
    public void deleteCart() {
        // Add an item to the cart
        driver.findElement(By.xpath("/html/body/div[4]/div[2]/div[3]/div[2]/form/input[1]")).click();

        boolean isItemPresent = isElementPresent(By.xpath("/html/body/div[4]/div[1]/div/table/tbody/tr[1]/td[2]"));
        Assert.assertTrue(isItemPresent);

        boolean isCartPresent = isElementPresent(By.id("cart"));
        Assert.assertTrue(isCartPresent);

        // Delete cart button
        driver.findElement(By.cssSelector("form.button_to:nth-child(5) > input:nth-child(2)")).click();

        // Cart does not exist
        isCartPresent = isElementPresent(By.cssSelector("#notice"));
        Assert.assertTrue(isCartPresent);
    }

    @Test
    public void searchItemTest() {
        String searchKeyword = "book";

        // Search
        driver.findElement(By.xpath("//*[@id=\"search_input\"]")).sendKeys(searchKeyword);

        boolean bookPresent = isElementPresent(By.xpath("/html/body/div[4]/div[2]/div[6]/h3/a"));
        Assert.assertTrue(bookPresent);

        // Search result
        String itemText = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div[6]/h3/a")).getText();
        Assert.assertEquals("Web Application Testing Book", itemText);
    }

    @Test
    public void searchByCategoryTest() {
        // Click book category button
        driver.findElement(By.xpath("/html/body/div[2]/div/ul/li[3]/a")).click();

        boolean bookPresent = isElementPresent(By.xpath("/html/body/div[4]/div[2]/div[6]/h3/a"));
        Assert.assertTrue(bookPresent);

        // Search result
        String itemText = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div[6]/h3/a")).getText();
        Assert.assertEquals("Web Application Testing Book", itemText);
    }

    @Test
    public void purchaseItemTest() {
        String username = "username";
        String address = "address123";
        String email = "user@mail";

        addProductTest();

        // Checkout button
        driver.findElement(By.xpath("/html/body/div[4]/div[1]/div/form[2]/input")).click();

        boolean checkoutPage = isElementPresent(By.xpath("//*[@id=\"order_page\"]"));
        Assert.assertTrue(checkoutPage);

        // Name, address, email
        driver.findElement(By.xpath("//*[@id=\"order_name\"]")).sendKeys(username);
        driver.findElement(By.xpath("//*[@id=\"order_address\"]")).sendKeys(address);
        driver.findElement(By.xpath("//*[@id=\"order_email\"]")).sendKeys(email);

        // Payment option selector
        By selectOptionBy = By.id("order_pay_type");
        Assert.assertTrue(isElementPresent(selectOptionBy));
        driver.findElement(selectOptionBy).click();

        // Choose "check" option
        Assert.assertTrue(isElementPresent(By.xpath("/html/body/div[4]/div[2]/div/form/div[4]/select/option[2]")));
        driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/form/div[4]/select/option[2]")).click();

        // Purchase confirmed
        driver.findElement(By.name("commit")).click();

        String confirmationText = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/h3")).getText();
        Assert.assertEquals("Thank you for your order", confirmationText);

    }

    @Test
    public void checkOtherCategoryItems() {
        // Supposed to fail
        // Bug: "other" category should contain items that are not included in either "Sunglasses" nor "Books" category

        // Check "Sunglasses" category
        driver.findElement(By.xpath("/html/body/div[2]/div/ul/li[2]/a")).click();
        List<WebElement> sunglassItems = driver.findElements(By.className("entry"));

        // Check "Books" category
        driver.findElement(By.xpath("/html/body/div[2]/div/ul/li[3]/a")).click();
        List<WebElement> bookItems = driver.findElements(By.className("entry"));

        // Check "Other" category
        driver.findElement(By.xpath("/html/body/div[2]/div/ul/li[4]/a")).click();
        List<WebElement> otherItems = driver.findElements(By.className("entry"));

        for (WebElement item : otherItems) {
            for (WebElement sunglassItem : sunglassItems) {
                assertNotEquals("Item from 'Sunglasses' found in 'Other'", sunglassItem.getText(), item.getText());
            }
            for (WebElement bookItem : bookItems) {
                assertNotEquals("Item from 'Books' found in 'Other'", bookItem.getText(), item.getText());
            }
        }
    }

}
