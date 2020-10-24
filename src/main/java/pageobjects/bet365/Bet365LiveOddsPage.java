package pageobjects.bet365;

import driver.Engine;
import driver.PatientWebDriver;
import elements.PageElement;
import model.Fixture;
import model.FixtureStatistics;
import model.TeamStats;
import model.Time;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import synchronization.Synchronizer;
import utils.ExtendedExpectedConditions;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class Bet365LiveOddsPage extends Bet365BasePage {

    private static final String FIXTURES_XPATH = "//div[@class='ovm-Fixture_Container']";
    private static final PageElement syncElement = new PageElement(By.xpath(FIXTURES_XPATH));

    @Override
    public Synchronizer synchronizer() {
        return super.synchronizer()
                .and(ExtendedExpectedConditions.waitForPageElementToBePresent(syncElement));
    }

    public Map<String, Fixture> getFixtures() {
        Map<String, Fixture> map = new HashMap<>();
        List<WebElement> elements = Engine.getDriver().findElements(By.xpath(FIXTURES_XPATH));
        for (WebElement element : elements) {
            Fixture fixture = fixtureContainerElementToFixture(element);
            map.put(fixture.getTeam1Name() + "_" + fixture.getTeam2Name(), fixture);
        }
        return map;
    }

    private Fixture fixtureContainerElementToFixture(WebElement element) {
        String timeString = element.findElement(By.xpath(".//div[@class='ovm-FixtureDetailsTwoWay_Timer ovm-InPlayTimer ']")).getText();
        String[] split = timeString.split(":");
        Time time = new Time(Integer.parseInt(split[0]), Integer.parseInt(split[1]));

        WebElement teamsWrapper = element.findElement(By.xpath(".//div[@class='ovm-FixtureDetailsTwoWay_TeamsWrapper']"));
        List<String> teams = teamsWrapper.findElements(By.xpath(".//div[@class='ovm-FixtureDetailsTwoWay_TeamName ']"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());

        WebElement oddsWrapper = element.findElement(By.xpath(".//div[@class='ovm-HorizontalMarket_Participants']"));
        List<WebElement> elements = oddsWrapper.findElements(By.xpath(".//span[@class='ovm-ParticipantOddsOnly_Odds']"));

        Double team1odds = null;
        Double drawOdds = null;
        Double team2odds = null;

        if (elements.size() == 3) {

            ToDoubleFunction<String> stringToDoubleFunction = s -> {
                if (s.isEmpty()) {
                    return -1;
                }
                return Double.parseDouble(s);
            };

            List<Double> odds = elements
                    .stream()
                    .map(WebElement::getText)
                    .mapToDouble(stringToDoubleFunction)
                    .boxed()
                    .collect(Collectors.toList());

            team1odds = odds.get(0);
            drawOdds = odds.get(1);
            team2odds = odds.get(2);
        }

        return new Fixture(
                time,
                teams.get(0),
                teams.get(1),
                team1odds,
                drawOdds,
                team2odds,
                element);
    }

    public FixtureStatistics getFixtureStats(Fixture fixture) {
        activateStats(fixture);

        WebElement wheelWrapper = Engine.getDriver().findElement(By.xpath("//div[@class='ml-WheelChart ']"));

        WebElement scoreWrapper = Engine.getDriver().findElement(By.xpath("//div[@class='lsb-ScoreBasedScoreboard ']"));

        List<Integer> score = scoreWrapper.findElements(By.xpath(".//span[@class='lsb-ScoreBasedScoreboard_TeamScore ']"))
                .stream()
                .mapToInt(e -> Integer.parseInt(e.getText()))
                .boxed()
                .collect(Collectors.toList());

        By angrebXpath = By.xpath("//div[text()='Angreb']/following-sibling::div[@class='ml-WheelChart_Container ']");
        By farligeAngrebXpath = By.xpath("//div[text()='Farlige angreb']/following-sibling::div[@class='ml-WheelChart_Container ']");
        By besiddelseXpath = By.xpath("//div[text()='Besiddelse %']/following-sibling::div[@class='ml-WheelChart_Container ']");

        By team1TextXpath = By.xpath(".//div[@class='ml-WheelChart_Team1Text ']");
        By team2TextXpath = By.xpath(".//div[@class='ml-WheelChart_Team2Text ']");

        Integer team1Angreb = null;
        Integer team2Angreb = null;
        Integer team1FarligeAngreb = null;
        Integer team2FarligeAngreb = null;
        Integer team1Besiddelse = null;
        Integer team2Besiddelse = null;

        try {
            WebElement angrebWrapper = wheelWrapper.findElement(angrebXpath);
            team1Angreb = Integer.parseInt(angrebWrapper.findElement(team1TextXpath).getText());
            team2Angreb = Integer.parseInt(angrebWrapper.findElement(team2TextXpath).getText());
        } catch (NoSuchElementException e) {
            System.out.println("Angreb not found!");
        }

        try {
            WebElement farligeAngrebWrapper = wheelWrapper.findElement(farligeAngrebXpath);
            team1FarligeAngreb = Integer.parseInt(farligeAngrebWrapper.findElement(team1TextXpath).getText());
            team2FarligeAngreb = Integer.parseInt(farligeAngrebWrapper.findElement(team2TextXpath).getText());
        } catch (NoSuchElementException e) {
            System.out.println("Farlige angreb not found!");
        }

        try {
            WebElement besiddelseWrapper = wheelWrapper.findElement(besiddelseXpath);
            team1Besiddelse = Integer.parseInt(besiddelseWrapper.findElement(team1TextXpath).getText());
            team2Besiddelse = Integer.parseInt(besiddelseWrapper.findElement(team2TextXpath).getText());
        } catch (NoSuchElementException e) {
            System.out.println("Beisddelse not found!");
        }

        WebElement skudWrapper = Engine.getDriver().findElement(By.xpath(".//div[@class='ml1-StatsLower_MiniBarsCollection ']"));

        WebElement skudPaaMaalWrapper = skudWrapper.findElement(By.xpath(".//h4[text()='Skud på mål']/following-sibling::div"));
        WebElement skudVedSidenAfMaalWrapper = skudWrapper.findElement(By.xpath(".//h4[text()='Ved siden af mål']/following-sibling::div"));

        By team1SkudXpath = By.xpath(".//b[contains(@class, 'ml-ProgressBar_MiniBarValue-1')]");
        By team2SkudXpath = By.xpath(".//b[contains(@class, 'ml-ProgressBar_MiniBarValue-2')]");

        int team1SkudPaaMaal = Integer.parseInt(skudPaaMaalWrapper.findElement(team1SkudXpath).getText());
        int team2SkudPaaMaal = Integer.parseInt(skudPaaMaalWrapper.findElement(team2SkudXpath).getText());

        int team1SkudVedSidenAfMaal = Integer.parseInt(skudVedSidenAfMaalWrapper.findElement(team1SkudXpath).getText());
        int team2SkudVedSidenAfMaal = Integer.parseInt(skudVedSidenAfMaalWrapper.findElement(team2SkudXpath).getText());

        TeamStats team1 = new TeamStats(team1Angreb, team1FarligeAngreb, team1Besiddelse, team1SkudPaaMaal, team1SkudVedSidenAfMaal, score.get(0));
        TeamStats team2 = new TeamStats(team2Angreb, team2FarligeAngreb, team2Besiddelse, team2SkudPaaMaal, team2SkudVedSidenAfMaal, score.get(1));

        return new FixtureStatistics(team1, team2);
    }

    public List<FixtureStatistics> getFixtureStats(List<Fixture> fixtures) {
        return fixtures
                .stream()
                .map(this::getFixtureStats)
                .collect(Collectors.toList());
    }

    public Bet365LiveOddsPage activateStats(Fixture fixture) {
        WebElement element = fixture.getContainer()
                .findElement(By.xpath("./following-sibling::div[@class='ovm-MediaIconContainer ']//div[contains(@class, 'me-MediaButtonLoader')]"));
        element.click();

        PageElement teamWrapper = new PageElement(By.xpath(".//div[@class='lsb-ScoreBasedScoreboard ']"));

        new FluentWait<>(Engine.getDriver())
                .pollingEvery(Duration.ofMillis(Engine.getPolling()))
                .withTimeout(Duration.ofMillis(Engine.getTimeout()))
                .until((Function<PatientWebDriver, Object>)
                        patientWebDriver -> teamWrapper.findElements(By.xpath(".//div[@class='lsb-ScoreBasedScoreboard_TeamName ']"))
                                .get(0)
                                .getText()
                                .contains(fixture.getTeam1Name()));


        new FluentWait<>(Engine.getDriver())
                .pollingEvery(Duration.ofMillis(Engine.getPolling()))
                .withTimeout(Duration.ofMillis(Engine.getTimeout()))
                .until((Function<PatientWebDriver, Object>)
                        patientWebDriver -> teamWrapper.findElements(By.xpath(".//div[@class='lsb-ScoreBasedScoreboard_TeamName ']"))
                                .get(1)
                                .getText()
                                .contains(fixture.getTeam2Name()));

        return this;
    }
}
