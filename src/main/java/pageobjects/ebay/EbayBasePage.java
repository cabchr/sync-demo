package pageobjects.ebay;

import utils.ExtendedExpectedConditions;
import elements.PageElement;
import org.openqa.selenium.By;
import synchronization.Synchronizer;

public class EbayBasePage {
    private static final PageElement inputSearch = new PageElement(By.xpath("//input[@placeholder='Search for anything']"));
    private static final PageElement buttonSearch = new PageElement(By.xpath("//input[@value='Search']"));

    public EbayBasePage() {
        ensureSyncronized();
    }

    public void ensureSyncronized() {
        Synchronizer synchronizer = synchronizer();
        if (!synchronizer.isSynchronized()) {
            ensureSyncronized();
        }
    }

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
