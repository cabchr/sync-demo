package driver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Engine {
    private static PatientWebDriver driver;
    private static final int polling = 200;
    private static final long timeout = 5000;

    private Engine() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        ChromeDriver chromeDriver = new ChromeDriver(options);
        driver = new PatientWebDriver(chromeDriver);
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("source", "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        }};

        chromeDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", map);
        Engine.driver.manage().window().maximize();
    }

    public static PatientWebDriver getDriver() {
        if (driver == null) {
            new Engine();
        }
        return driver;
    }

    public static int getPolling() {
        return polling;
    }

    public static long getTimeout() {
        return timeout;
    }
}
