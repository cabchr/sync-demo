package login;

import driver.Engine;
import pageobjects.ebay.EbayStartPage;

public class Login {
    public static EbayStartPage ebay() {
        Engine.getDriver().get("https://www.ebay.com");
        return new EbayStartPage();
    }
}
