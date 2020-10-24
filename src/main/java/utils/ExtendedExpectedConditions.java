package utils;

import driver.PatientWebDriver;
import elements.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.function.Function;

public class ExtendedExpectedConditions {
    public static Function<PatientWebDriver, Boolean> waitForPageElementToBePresent(PageElement element) {
        return webDriver -> element.isDisplayed();
    }

    public static Function<PatientWebDriver, Boolean> waitForPageElementToContainText(PageElement element, String text) {
        return webDriver -> element.getText().contains(text);
    }

}
