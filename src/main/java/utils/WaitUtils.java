package utils;

import driver.Engine;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {
    private static WebDriver driver = Engine.getDriver();

    public static void sleep(int sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitForPageToLoad() {
        System.out.println("Waiting for page to load:");

        long startTime = System.currentTimeMillis();

        WebDriverWait wait = new WebDriverWait(driver, Engine.getTimeout());
        wait.until(currentContentIsLoaded());
        System.out.println("Page loaded in " + (System.currentTimeMillis() - startTime) + " milliseconds");
    }

    private static ExpectedCondition<Boolean> currentContentIsLoaded() {
        return webDriver -> {
            JavascriptExecutor executor = (JavascriptExecutor) webDriver;
            assert executor != null;
            return executor.executeScript("return document.readyState").toString().equals("complete") &&
                    (boolean) executor.executeScript("return jQuery.active == 0");
        };
    }
}
