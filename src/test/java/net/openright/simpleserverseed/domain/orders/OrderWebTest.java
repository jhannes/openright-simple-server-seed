package net.openright.simpleserverseed.domain.orders;

import net.openright.infrastructure.test.WebTestUtil;
import net.openright.simpleserverseed.application.SeedAppServer;
import net.openright.simpleserverseed.application.SimpleseedTestConfig;
import net.openright.simpleserverseed.domain.products.Product;
import net.openright.simpleserverseed.domain.products.ProductRepository;
import net.openright.simpleserverseed.domain.products.ProductRepositoryTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderWebTest {
    private static SimpleseedTestConfig config = SimpleseedTestConfig.instance();
    private static SeedAppServer server = new SeedAppServer(config);
    private static WebDriver browser;
    private static WebDriverWait wait;
    private OrdersRepository orderRepository = new OrdersRepository(config);
    private ProductRepository productRepository = new ProductRepository(config);

    @BeforeClass
    public static void startServer() throws Exception {
        server.start(0);
    }

    @BeforeClass
    public static void startBrowser() throws IOException {
        browser = WebTestUtil.createDriver(config.getWebDriverName());
        wait = new WebDriverWait(browser, 4);
    }

    @AfterClass
    public static void stopClient() {
        browser.quit();
    }

    @Before
    public void goToFrontPage() {
        browser.manage().deleteAllCookies();
        browser.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
        browser.get(server.getURI().toString());
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("nav")));
    }

    @Test
    public void shouldSeeCurrentProducts() throws Exception {
        Product order = ProductRepositoryTest.sampleProduct();
        productRepository.insert(order);
        browser.get(server.getURI().toString() + "#products/list");

        List<String> orders = browser.findElement(By.id("products"))
                .findElements(By.tagName("li"))
                .stream().map(WebElement::getText).collect(Collectors.toList());

        assertThat(orders).contains("Product: " + order.getTitle());
    }

    @Test
    public void shouldAddProduct() throws Exception {
        Product newProduct = ProductRepositoryTest.sampleProduct();

        browser.findElement(By.linkText("Products")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("addProduct")));
        browser.findElement(By.id("addProduct")).click();

        WebElement titleField = browser.findElement(By.name("product[title]"));
        titleField.clear();
        titleField.sendKeys(newProduct.getTitle());
        browser.findElement(By.name("product[price]"))
                .sendKeys(String.valueOf(newProduct.getPrice()));
        browser.findElement(By.name("product[description]"))
                .sendKeys(String.valueOf(newProduct.getDescription()));
        titleField.submit();

        browser.findElement(By.id("products"));

        Product product = productRepository.list().stream()
                .filter(p -> p.getTitle().equals(newProduct.getTitle()))
                .findFirst().get();
        assertThat(product)
                .isEqualToIgnoringGivenFields(newProduct, "id");
    }

    @Test
    public void shouldSeeCurrentOrders() throws Exception {
        Order order = OrderRepositoryTest.sampleOrder();
        orderRepository.insert(order);
        browser.get(server.getURI().toString());

        List<String> orders = browser.findElement(By.id("ordersList"))
            .findElements(By.tagName("li"))
            .stream().map(WebElement::getText).collect(Collectors.toList());

        assertThat(orders).contains("Order: " + order.getTitle());
    }

    @Test
    public void shouldDisplayErrorOnInvalidOrderId() {
        browser.get(server.getURI().toString() + "#orders/edit/-1");

        WebElement notification = browser.findElement(By.cssSelector("#notifications .notify"));
        wait.until(ExpectedConditions.visibilityOf(notification));
        assertThat(notification.getAttribute("class")).contains("warning");
        assertThat(notification.findElement(By.className("title")).getText()).isEqualTo("Not found");
    }

    @Test
    public void shouldEditProduct() throws Exception {
        Product product = ProductRepositoryTest.sampleProduct();
        productRepository.insert(product);

        browser.findElement(By.linkText("Products")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("addProduct")));
        browser.findElement(By.linkText(product.getTitle())).click();

        WebElement titleField = browser.findElement(By.name("product[title]"));
        titleField.clear();
        titleField.sendKeys("New title");
        titleField.submit();

        browser.findElement(By.id("products"));
        assertThat(productRepository.retrieve(product.getId()).getTitle())
            .isEqualTo("New title");
    }

    @Test
    public void shouldAddOrder() throws Exception {
        Product product = ProductRepositoryTest.sampleProduct();
        productRepository.insert(product);

        browser.findElement(By.id("addOrder")).click();

        String orderTitle = OrderRepositoryTest.sampleOrder().getTitle();

        WebElement titleField = browser.findElement(By.name("order[title]"));
        titleField.clear();
        titleField.sendKeys(orderTitle);

        browser.findElement(By.id("addOrderLine")).click();

        browser.findElement(By.cssSelector("#orderLines .productSelect"))
            .findElement(optionWithText(product.getTitle()))
            .click();
        browser.findElement(By.cssSelector("#orderLines input[name='order[orderlines][][amount]']"))
            .sendKeys("120");

        titleField.submit();

        browser.findElement(By.id("ordersList"));

        assertThat(orderRepository.list().stream().map(Order::getTitle).collect(Collectors.toList()))
            .contains(orderTitle);
    }

    @Test
    public void shouldUpdateExistingOrder() throws Exception {
        Product productToBeDeleted = ProductRepositoryTest.sampleProduct();
        productRepository.insert(productToBeDeleted);
        Product product = ProductRepositoryTest.sampleProduct();
        productRepository.insert(product);

        Order order = OrderRepositoryTest.sampleOrder();
        order.addOrderLine(productToBeDeleted.getId(), 200);
        order.addOrderLine(product.getId(), 100);
        orderRepository.insert(order);

        browser.get(server.getURI().toString() + "#orders/edit/" + order.getId());

        WebElement titleField = browser.findElement(By.name("order[title]"));
        assertThat(titleField.getAttribute("value")).isEqualTo(order.getTitle());

        browser.findElement(By.className("deleteOrderLine")).click();

        WebElement amountField = browser.findElement(By.name("order[orderlines][][amount]"));
        assertThat(amountField.getAttribute("value")).isEqualTo("100");
        amountField.clear();
        amountField.sendKeys("1000");
        amountField.submit();

        browser.findElement(By.id("ordersList"));

        assertThat(orderRepository.retrieve(order.getId()).getOrderLines()).hasSize(1);
        assertThat(orderRepository.retrieve(order.getId()).getOrderLines().get(0).getAmount())
                .isEqualTo(1000);
    }

    private By optionWithText(String optionText) {
        return By.xpath("option[text() = '" + optionText + "']");
    }

}
