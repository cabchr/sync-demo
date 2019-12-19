import login.Login;
import org.testng.annotations.Test;
import pageobjects.ebay.EbaySearchPage;
import pageobjects.ebay.EbayStartPage;

public class EbayTest {
    @Test
    public void test() {
        EbayStartPage page = Login.ebay();

        page.search("Test");
        EbaySearchPage test = page.activateSearch();

        page.search("Test 2");
        page.activateSearch();

        page.search("Test 3");
        page.search("Test 4");
    }
}
