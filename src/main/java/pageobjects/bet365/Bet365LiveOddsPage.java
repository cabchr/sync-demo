package pageobjects.bet365;

import driver.Engine;
import driver.PatientWebDriver;
import elements.PageElement;
import model.Fixture;
import model.FixtureStatistics;
import model.TeamStats;
import model.Time;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import synchronization.Synchronizer;
import utils.ExtendedExpectedConditions;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
        int i = 1;

        for (WebElement element : elements) {
            Fixture fixture = fixtureContainerElementToFixture(i);
            if (fixture != null) {
                map.put(fixture.getTeam1Name() + "_" + fixture.getTeam2Name(), fixture);
            }
            i++;
        }
        return map;
    }

    private Fixture fixtureContainerElementToFixture(int index) {
        String rootContainerXpath = String.format("(.//div[@class='ovm-Fixture_Container'])[%d]", index);
        PageElement root = new PageElement(By.xpath(rootContainerXpath));
        try {
            if (!root.isDisplayed()) {
                return null;
            }
        } catch (TimeoutException e) {
            return null;
        }

        String timeString = new PageElement(By.xpath(rootContainerXpath + "//div[@class='ovm-FixtureDetailsTwoWay_Timer ovm-InPlayTimer ']")).getText();
        String[] split = timeString.split(":");
        Time time = new Time(Integer.parseInt(split[0]), Integer.parseInt(split[1]));

        String teamsRootXpath = "//div[@class='ovm-FixtureDetailsTwoWay_TeamsWrapper']";
        String teamsXpath = "//div[@class='ovm-FixtureDetailsTwoWay_TeamName ']";

        String teamXpath = "(" + rootContainerXpath + teamsRootXpath + teamsXpath + ")";
        PageElement team1 = new PageElement(By.xpath(teamXpath + "[1]"));
        PageElement team2 = new PageElement(By.xpath(teamXpath + "[2]"));
        String team1Name;
        String team2Name;

        try {
            team1Name = team1.getText();
            team2Name = team2.getText();
        } catch (TimeoutException e) {
            System.out.println("Teamname not found!");
            return null;
        }

        String fixtureContainer = String.format(".//div[%s]/parent::div/following-sibling::div//div[%s]/ancestor::div[@class='ovm-Fixture_Container']",
                generateTeamXpath(team1Name), generateTeamXpath(team2Name));

        String oddsRootXpath = "//div[@class='ovm-HorizontalMarket_Participants']";
        String oddsIntermediaryXpath = "//div";
        String oddsXpath = "//span";

        PageElement container = new PageElement(By.xpath(fixtureContainer));

        if (!container.isPresent()) {
            System.out.println("Container not found!");
            return null;
        }

        PageElement team1OddsElement = new PageElement(By.xpath(fixtureContainer + oddsRootXpath + oddsIntermediaryXpath + "[1]" + oddsXpath));
        PageElement drawOddsElement = new PageElement(By.xpath(rootContainerXpath + oddsRootXpath + oddsIntermediaryXpath + "[2]" + oddsXpath));
        PageElement team2OddsElement = new PageElement(By.xpath(rootContainerXpath + oddsRootXpath + oddsIntermediaryXpath + "[3]" + oddsXpath));

        String team1odds = "";
        String drawOdds = "";
        String team2odds = "";
        try {
            team1odds = team1OddsElement.getText();
        } catch (NoSuchElementException | TimeoutException e ) {
            System.out.println("Team1Odds not found!");
            return null;
        }

        try {
            drawOdds = drawOddsElement.getText();
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("DrawOdds not found!");

            return null;
        }

        try {
            team2odds = team2OddsElement.getText();
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("Team2odds not found!");
            return null;
        }

        return new Fixture(
                time,
                team1Name,
                team2Name,
                !"".equals(team1odds) && !"FS".equals(team1odds) ? Double.parseDouble(team1odds) : -1d,
                !"".equals(drawOdds) && !"FS".equals(drawOdds) ? Double.parseDouble(drawOdds) : -1d,
                !"".equals(team2odds) && !"FS".equals(team2odds) ? Double.parseDouble(team2odds) : -1d,
                new PageElement(By.xpath(fixtureContainer)));
    }

    public FixtureStatistics getFixtureStats(Fixture fixture) {
        try {

        } catch (Exception e) {
            return null;
        }

        if (!hasStats(fixture)) {
            return null;
        }

        Bet365LiveOddsPage page = activateStats(fixture);
        if (page == null) {
            return null;
        }

        PageElement wheelWrapper = new PageElement(By.xpath("//div[@class='ml-WheelChart ']"));
        PageElement scoreWrapper = new PageElement(By.xpath("//div[@class='lsb-ScoreBasedScoreboard ']"));

        if (!wheelWrapper.isPresent()) {
            return null;
        }

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
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("Angreb not found!");
        }

        try {
            WebElement farligeAngrebWrapper = wheelWrapper.findElement(farligeAngrebXpath);
            team1FarligeAngreb = Integer.parseInt(farligeAngrebWrapper.findElement(team1TextXpath).getText());
            team2FarligeAngreb = Integer.parseInt(farligeAngrebWrapper.findElement(team2TextXpath).getText());
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("Farlige angreb not found!");
        }

        try {
            WebElement besiddelseWrapper = wheelWrapper.findElement(besiddelseXpath);
            team1Besiddelse = Integer.parseInt(besiddelseWrapper.findElement(team1TextXpath).getText());
            team2Besiddelse = Integer.parseInt(besiddelseWrapper.findElement(team2TextXpath).getText());
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("Besiddelse not found!");
        }

        PageElement skudWrapper = new PageElement(By.xpath(".//div[@class='ml1-StatsLower_MiniBarsCollection ']"));

        if(!skudWrapper.isPresent()) {
            return null;
        }

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

    private boolean hasStats(Fixture fixture) {
        PageElement container = fixture.getContainer();
        if (!container.isPresent()) {
            return false;
        }
        return !container
                .findElements(By.xpath("./following-sibling::div[@class='ovm-MediaIconContainer ']//div[contains(@class, 'me-MediaButtonLoader')]"))
                .isEmpty();
    }

    public Bet365LiveOddsPage activateStats(Fixture fixture) {
        WebElement element = null;
        try {
            PageElement container = fixture.getContainer();
            if (!container.isPresent()) {
                return null;
            }

            element = container
                    .findElement(By.xpath("./following-sibling::div[@class='ovm-MediaIconContainer ']//div[contains(@class, 'me-MediaButtonLoader')]"));
            try {
                element.click();
            } catch (ElementNotInteractableException e) {
                return null;
            }

        } catch (TimeoutException e) {
            return this;
        }

        try {
            PageElement teamWrapper = new PageElement(By.xpath(".//div[@class='lsb-ScoreBasedScoreboard ']"));
            new FluentWait<>(Engine.getDriver())
                    .pollingEvery(Duration.ofMillis(Engine.getPolling()))
                    .withTimeout(Duration.ofMillis(Engine.getTimeout() * 2))
                    .until((Function<PatientWebDriver, Object>)
                            patientWebDriver -> {
                                String text = "";
                                long start = System.currentTimeMillis();

                                while (System.currentTimeMillis() - start < Engine.getTimeout()) {
                                    try {
                                        List<WebElement> elements = teamWrapper.findElements(By.xpath(".//div[@class='lsb-ScoreBasedScoreboard_TeamName ']"));

                                        WebElement ele = elements.get(0);

                                        text = ele.getText();

                                        if (!text.isEmpty()) {
                                            break;
                                        }
                                    } catch (StaleElementReferenceException e) {
                                    }
                                }

                                return text.contains(fixture.getTeam1Name());
                            });


            new FluentWait<>(Engine.getDriver())
                    .pollingEvery(Duration.ofMillis(Engine.getPolling()))
                    .withTimeout(Duration.ofMillis(Engine.getTimeout() * 2))
                    .until((Function<PatientWebDriver, Object>)
                            patientWebDriver -> {
                                String text = "";
                                long start = System.currentTimeMillis();

                                while (System.currentTimeMillis() - start < Engine.getTimeout()) {
                                    try {
                                        List<WebElement> elements = teamWrapper.findElements(By.xpath(".//div[@class='lsb-ScoreBasedScoreboard_TeamName ']"));

                                        WebElement ele = elements.get(1);

                                        text = ele.getText();

                                        if (!text.isEmpty()) {
                                            break;
                                        }
                                    } catch (StaleElementReferenceException e) {
                                    }
                                }

                                return text.contains(fixture.getTeam2Name());
                            });
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return this;
    }

    private String generateTeamXpath(String teamName) {
        String contains = "contains(text(), \"%s\")";
        String result = "";
        String[] team = teamName.split(" ");

        for (int i = 0; i < team.length; i++) {
            result += String.format(contains, team[i].replace("(", "").replace(")", ""));
            if (i == team.length - 1) {
                break;
            }
            result += " and ";
        }
        return result;
    }
}
