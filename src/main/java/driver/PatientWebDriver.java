package driver;

import elements.PageElement;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PatientWebDriver implements WebDriver, JavascriptExecutor {

    private final WebDriver driver;

    PatientWebDriver(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void get(String s) {
        driver.get(s);
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return driver.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return driver.findElement(by);
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public void close() {
        driver.close();
    }

    @Override
    public void quit() {
        driver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return driver.navigate();
    }

    @Override
    public Options manage() {
        return driver.manage();
    }

    public WebElement findElementPatiently(PageElement element) {
        return findElementPatiently(element.by);
    }

    public WebElement findElementPatiently(By by) {
        FluentWait<WebDriver> wait = new FluentWait<>(this);
        wait.withTimeout(Duration.ofMillis(Engine.getTimeout()));
        wait.pollingEvery(Duration.ofMillis(Engine.getPolling()));

        wait.ignoreAll(Arrays.asList(
                        NoSuchElementException.class,
                        ElementNotVisibleException.class,
                        NotFoundException.class,
                        StaleElementReferenceException.class));

        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    @Override
    public Object executeScript(String s, Object... objects) {
        return ((JavascriptExecutor)driver).executeScript(s, objects);
    }

    @Override
    public Object executeAsyncScript(String s, Object... objects) {
        return ((JavascriptExecutor) driver).executeAsyncScript(s, objects);
    }

}
