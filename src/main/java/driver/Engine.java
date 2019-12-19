package driver;

import org.openqa.selenium.chrome.ChromeDriver;

public class Engine {
    private static PatientWebDriver driver;
    private static final int polling = 200;
    private static final long timeout = 5000;

    private Engine() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Casper\\Downloads\\chromedriver_win32 (1)\\chromedriver.exe");
        driver = new PatientWebDriver(new ChromeDriver());
        driver.manage().window().maximize();
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
