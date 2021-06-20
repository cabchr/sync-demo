package pageobjects.bet365;

import elements.PageElement;
import org.openqa.selenium.By;
import pageobjects.BasePage;
import synchronization.Synchronizer;
import utils.ExtendedExpectedConditions;

public class Bet365BasePage extends BasePage {
    private static final PageElement btnLiveOdds = new PageElement(By.xpath(".//div[@class='hm-MainHeaderWide ']//div[text()='Liveodds']"));
    private static final PageElement imgLogo = new PageElement(By.xpath(".//div[@class='hm-MainHeaderLogoWide_Bet365LogoImage ']"));

    @Override
    public Synchronizer synchronizer() {
        return new Synchronizer(ExtendedExpectedConditions.waitForPageElementToBePresent(imgLogo));
    }

    public Bet365LiveOddsPage activateLiveOdds() {
        try {
            btnLiveOdds.click();
        } catch (Exception e) {
            btnLiveOdds.click();
        }
        return new Bet365LiveOddsPage();
    }
}
