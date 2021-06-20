package elements;

import driver.Engine;
import driver.PatientWebDriver;
import org.openqa.selenium.*;
import utils.WaitUtils;

import java.util.List;

public class PageElement implements WebElement {

    public final By by;
    private PatientWebDriver driver = Engine.getDriver();

    public PageElement(By by) {
        this.by = by;
    }

    @Override
    public void click() {
        new ElementFinder(this, driver) {
            @Override
            public Object operation(PatientWebDriver driver, PageElement element) {
                element.getElement().click();
                return null;
            }
        }.perform();
    }

    @Override
    public void submit() {
        new ElementFinder(this, driver) {
            @Override
            public Object operation(PatientWebDriver driver, PageElement element) {
                element.getElement().submit();
                return null;
            }
        }.perform();
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        new ElementFinder(this, driver) {
            @Override
            public Object operation(PatientWebDriver driver, PageElement element) {
                element.getElement().sendKeys(charSequences);
                return null;
            }
        }.perform();
    }

    @Override
    public void clear() {
        new ElementFinder(this, driver) {
            @Override
            public Object operation(PatientWebDriver driver, PageElement element) {
                element.getElement().clear();
                return null;
            }
        }.perform();
    }

    @Override
    public String getTagName() {
        return (String) new ElementFinder(this, driver) {
            @Override
            public Object operation(PatientWebDriver driver, PageElement element) {
                return element.getElement().getTagName();
            }
        }.perform();
    }

    @Override
    public String getAttribute(String s) {
        return (String) new ElementFinder(this, driver) {
            @Override
            public Object operation(PatientWebDriver driver, PageElement element) {
                return element.getElement().getAttribute(s);
            }
        }.perform();
    }

    @Override
    public boolean isSelected() {
        return (boolean) new ElementFinder(this, driver) {
            @Override
            public Object operation(PatientWebDriver driver, PageElement element) {
                return element.getElement().isSelected();
            }
        }.perform();
    }

    @Override
    public boolean isEnabled() {
        return (boolean) new ElementFinder(this, driver) {
            @Override
            public Object operation(PatientWebDriver driver, PageElement element) {
                return element.getElement().isEnabled();
            }
        }.perform();
    }

    @Override
    public String getText() {
        return (String) new ElementFinder(this, driver) {
            @Override
            public Object operation(PatientWebDriver driver, PageElement element) {
                return element.getElement().getText();
            }
        }.perform();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return getElement().findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return (WebElement) new ElementFinder(this, driver) {
            @Override
            public Object operation(PatientWebDriver driver, PageElement element) {
                return element.getElement().findElement(by);
            }
        }.perform();
    }

    @Override
    public boolean isDisplayed() {
        return (boolean) new ElementFinder(this, driver) {
            @Override
            public Object operation(PatientWebDriver driver, PageElement element) {
                return element.getElement().isDisplayed();
            }
        }.perform();
    }

    @Override
    public Point getLocation() {
        return getElement().getLocation();
    }

    @Override
    public Dimension getSize() {
        return getElement().getSize();
    }

    @Override
    public Rectangle getRect() {
        return getElement().getRect();
    }

    @Override
    public String getCssValue(String s) {
        return getElement().getCssValue(s);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
        return getElement().getScreenshotAs(outputType);
    }

    private static abstract class ElementFinder {
        PageElement element;
        PatientWebDriver driver;
        long timeout;
        int polling;

        ElementFinder(PageElement element, PatientWebDriver driver) {
            this.element = element;
            this.driver = driver;
            this.timeout = Engine.getTimeout();
            this.polling = Engine.getPolling();
        }

        Object perform() {
            long startTime = System.currentTimeMillis();
            while (true) {
                try {
                    return operation(driver, element);
                } catch (WebDriverException e) {
                    if (System.currentTimeMillis() - startTime > timeout) {
                        throw e;
                    }
                    WaitUtils.sleep(polling);
                }
            }
        }

        public abstract Object operation(PatientWebDriver driver, PageElement element);

    }

    public WebElement getElement() {
        return driver.findElementPatiently(by);
    }

    public boolean isPresentPatiently() {
        try {
            return !findElements(by).isEmpty();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isPresent() {
        return !Engine.getDriver().findElements(by).isEmpty();
    }
}
