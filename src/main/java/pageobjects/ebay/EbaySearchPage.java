package pageobjects.ebay;

import utils.ExtendedExpectedConditions;
import elements.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import synchronization.Synchronizer;

import java.util.List;

public class EbaySearchPage extends EbayBasePage {

    private static final PageElement elementToSync = new PageElement(By.xpath("//div[@id='mainContent']//ul[contains(@class,'srp-results')]"));
    private static final PageElement listings = new PageElement(By.xpath("//div[@id='mainContent']//ul[contains(@class,'srp-results')]"));

    @Override
    public Synchronizer synchronizer() {
        return super.synchronizer().and(ExtendedExpectedConditions.waitForPageElementToBePresent(elementToSync));
    }

    public List<String> getListings() {
        for (WebElement element : listings.findElements(By.xpath("./li//div/a"))) {
            System.out.println(element.getText());
        }
        return null;
    }
}
