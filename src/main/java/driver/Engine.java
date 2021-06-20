package driver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.ExeUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Engine {
    private static PatientWebDriver driver;
    private static final int polling = 250;
    private static final long timeout = 2000;

    private Engine() throws IOException, InterruptedException {
        Runtime.getRuntime().exec("taskkill /F /IM " + "chromedriver_90_patched.exe");

        String filePath = "src/main/resources/chromedriver_90.exe";
        ExeUtils.replaceBytes(filePath);
        System.setProperty("webdriver.chrome.driver", filePath.replace(".exe", "_patched.exe"));

        Process process = Runtime.getRuntime().exec("\"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe\" --remote-debugging-port=9222 --incognito");
//        process.waitFor();

        ChromeOptions options = new ChromeOptions();
//        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--incognito");
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");

        ChromeDriver chromeDriver = new ChromeDriver(new ChromeDriverService.Builder()
                .usingPort(10000)
                .usingDriverExecutable(new File("src/main/resources/chromedriver_90_patched.exe")).build(), options);
        Map<String, Object> map = new HashMap<>() {{
            put("source", "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        }};

        chromeDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", map);

        Map<String, Object> map2 = new HashMap<>() {{
            put("source", "Object.defineProperty(navigator, 'maxTouchPoints', {get: () => 1}");
        }};

        chromeDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", map2);
        driver = new PatientWebDriver(chromeDriver);

        Engine.driver.manage().window().maximize();
    }

    public static PatientWebDriver getDriver() {
        if (driver == null) {
            try {
                new Engine();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
