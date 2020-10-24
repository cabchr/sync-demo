package pageobjects.bet365;

import elements.PageElement;
import org.openqa.selenium.By;
import pageobjects.BasePage;
import synchronization.Synchronizer;
import utils.ExtendedExpectedConditions;

import java.lang.reflect.InvocationTargetException;

public class Bet365BasePage extends BasePage {
    private static final PageElement btnLiveOdds = new PageElement(By.xpath(".//div[@class='hm-MainHeaderWide ']//div[text()='Liveodds']"));
    private static final PageElement imgLogo = new PageElement(By.xpath(".//div[@class='hm-MainHeaderLogoWide_Bet365LogoImage ']"));

    @Override
    public Synchronizer synchronizer() {
        return new Synchronizer(ExtendedExpectedConditions.waitForPageElementToBePresent(imgLogo));
    }

    public <T> T activateLiveOdds(Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        btnLiveOdds.click();
        return clazz.getDeclaredConstructor().newInstance();
    }
}
