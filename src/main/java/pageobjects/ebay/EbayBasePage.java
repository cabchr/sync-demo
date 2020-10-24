package pageobjects.ebay;

import pageobjects.BasePage;
import utils.ExtendedExpectedConditions;
import elements.PageElement;
import org.openqa.selenium.By;
import synchronization.Synchronizer;

public class EbayBasePage extends BasePage {
    private static final PageElement inputSearch = new PageElement(By.xpath("//input[@placeholder='Search for anything']"));
    private static final PageElement buttonSearch = new PageElement(By.xpath("//input[@value='Search']"));

    public Synchronizer synchronizer() {
        return new Synchronizer(ExtendedExpectedConditions.waitForPageElementToBePresent(inputSearch))
                .and(ExtendedExpectedConditions.waitForPageElementToBePresent(buttonSearch));
    }

    public EbayBasePage search(String searchString) {
        inputSearch.clear();
        inputSearch.sendKeys(searchString);
        return this;
    }

    public EbaySearchPage activateSearch() {
        buttonSearch.click();
        return new EbaySearchPage();
    }
}
