package login;

import driver.Engine;
import pageobjects.bet365.Bet365StartPage;
import pageobjects.ebay.EbayStartPage;

public class Login {

    private Login() {
    }

    public static EbayStartPage ebay() {
        Engine.getDriver().get("https://www.ebay.com");
        return new EbayStartPage();
    }

    public static Bet365StartPage bet365() {
        Engine.getDriver().get("https://www.bet365.dk");
        return new Bet365StartPage();
    }
}
