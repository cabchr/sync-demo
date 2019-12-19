package utils;

import driver.PatientWebDriver;
import elements.PageElement;

import java.util.function.Function;

public class ExtendedExpectedConditions {
    public static Function<PatientWebDriver, Boolean> waitForPageElementToBePresent(PageElement element) {
        return webDriver -> element.isDisplayed();
    }
}
