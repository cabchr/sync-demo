package pageobjects.bet365;

import driver.Engine;
import driver.PatientWebDriver;
import elements.PageElement;
import model.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import synchronization.Synchronizer;
import utils.ExtendedExpectedConditions;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static utils.PostUtils.postMatch;

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

    public Bet365LiveOddsPage postFixturesAndStats() throws IOException {
        List<WebElement> elements = Engine.getDriver().findElements(By.xpath(FIXTURES_XPATH));
        for (int i = 1; i <= elements.size(); i++) {
            Fixture fixture = fixtureContainerElementToFixture(i);
            if (fixture != null) {
                if (fixture.getTeam1Name().contains("Agua")) {
                    System.out.println(fixture);
                }
                FixtureStatistics fixtureStats = getFixtureStats(fixture);
                if (fixture.getTeam1Name().contains("Agua")) {
                    System.out.println(fixtureStats);
                }
                if (fixtureStats != null
                        && !fixture.getTeam1Name().contains("Esport")
                        && !fixture.getTeam2Name().contains("Esport")) {
                    Pair<Fixture, FixtureStatistics> fixtureAndStats = new Pair<>(fixture, fixtureStats);
                    if (fixture.getTeam1Name().contains("Agua")) {
                        System.out.println("posting agua");
                        postMatch(fixtureAndStats, true);
                    } else {
                        postMatch(fixtureAndStats);
                    }
                }
            }
        }
        return this;
    }

    private Fixture fixtureContainerElementToFixture(int index) {
        String rootContainerXpath = String.format("(.//div[@class='ovm-Fixture_Container'])[%d]", index);
        PageElement root = new PageElement(By.xpath(rootContainerXpath));
        try {
            if (!root.isDisplayed()) {
                System.out.println("Root not displayed");
                return null;
            }
        } catch (TimeoutException e) {
            System.out.println("Root not displayed");
            return null;
        }

        PageElement timerElement = new PageElement(By.xpath(rootContainerXpath + "//div[@class='ovm-FixtureDetailsTwoWay_Timer ovm-InPlayTimer ']"));

        if (!timerElement.isPresent()) {
            System.out.println("Timer not displayed");
            return null;
        }

        String timeString = timerElement.getText();
        int count = 0;
        while (timerElement.isPresent() && "".equals(timeString) && count++ < 5) {
            ((JavascriptExecutor) Engine.getDriver()).executeScript("arguments[0].scrollIntoView(true);", timerElement.getElement());
            timeString = timerElement.getText();
        }
        String[] split = timeString.split(":");
        Time time = null;
        try {
            time = new Time(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        } catch (NumberFormatException e) {
            System.out.println("Unable to find time");
            return null;
        }

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
        String oddsXpath = "//span[@class='ovm-ParticipantOddsOnly_Odds']";

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
            System.out.printf("{%s} Team1Odds not found!%n", team1Name);
            team1odds = "-1";
        }

        try {
            drawOdds = drawOddsElement.getText();
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("DrawOdds not found!");
            System.out.printf("{%s-%s} DrawOdds not found!%n", team1Name, team2Name);
            drawOdds = "-1";
        }

        try {
            team2odds = team2OddsElement.getText();
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.printf("{%s} Team2Odds not found!%n", team2Name);
            team2odds = "-1";
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
        if (!hasStats(fixture)) {
            System.out.printf("No stats found for fixture: {%s}%n", fixture);
            return null;
        }

        Bet365LiveOddsPage page = activateStats(fixture);
        if (page == null) {
            return null;
        }

        PageElement wheelWrapper = new PageElement(By.xpath("//div[@class='ml-WheelChart ']"));
        PageElement scoreWrapper = new PageElement(By.xpath("//div[@class='lsb-ScoreBasedScoreboard ']"));

        if (!wheelWrapper.isPresent()) {
            System.out.printf("WheelWrapper not found for fixture: {%s}%n", fixture);
            return null;
        }

        List<Integer> score = scoreWrapper.findElements(By.xpath(".//span[@class='lsb-ScoreBasedScoreboard_TeamScore ']"))
                .stream()
                .mapToInt(e -> Integer.parseInt(e.getText()))
                .boxed()
                .collect(Collectors.toList());

        String angrebXpath = "//div[text()='Angreb']/following-sibling::div[@class='ml-WheelChart_Container ']";
        String farligeAngrebXpath = "//div[text()='Farlige angreb']/following-sibling::div[@class='ml-WheelChart_Container ']";
        String besiddelseXpath = "//div[text()='Besiddelse %']/following-sibling::div[@class='ml-WheelChart_Container ']";

        String team1TextXpath = "//div[@class='ml-WheelChart_Team1Text ']";
        String team2TextXpath = "//div[@class='ml-WheelChart_Team2Text ']";

        Integer team1Angreb = null;
        Integer team2Angreb = null;
        Integer team1FarligeAngreb = null;
        Integer team2FarligeAngreb = null;
        Integer team1Besiddelse = null;
        Integer team2Besiddelse = null;

        try {
            team1Angreb = Integer.parseInt(new PageElement(By.xpath(angrebXpath + team1TextXpath)).getText());
            team2Angreb = Integer.parseInt(new PageElement(By.xpath(angrebXpath + team2TextXpath)).getText());
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.printf("{%s-%s} Angreb not found!%n", fixture.getTeam1Name(), fixture.getTeam2Name());
        }

        try {
            team1FarligeAngreb = Integer.parseInt(new PageElement(By.xpath(farligeAngrebXpath + team1TextXpath)).getText());
            team2FarligeAngreb = Integer.parseInt(new PageElement(By.xpath(farligeAngrebXpath + team2TextXpath)).getText());
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.printf("{%s-%s} Farlige angreb not found!%n", fixture.getTeam1Name(), fixture.getTeam2Name());
        }

        try {
            team1Besiddelse = Integer.parseInt(new PageElement(By.xpath(besiddelseXpath + team1TextXpath)).getText());
            team2Besiddelse = Integer.parseInt(new PageElement(By.xpath(besiddelseXpath + team2TextXpath)).getText());
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.printf("{%s-%s} Besiddelse not found%n", fixture.getTeam1Name(), fixture.getTeam2Name());
        }

        String skudWrapperXpath = "//div[@class='ml1-StatsLower_MiniBarsCollection ']";
        PageElement skudWrapper = new PageElement(By.xpath(skudWrapperXpath));

        try {
            if (!skudWrapper.isPresent()) {
                System.out.printf("SkudWrapper not found for fixture: {%s}%n", fixture);
                return null;
            }
        } catch (Exception e) {
            System.out.printf("SkudWrapper not found for fixture: {%s}%n", fixture);
            return null;
        }


        String skudPaaMaalXpath = "//h4[text()='Skud på mål']/following-sibling::div";
        String skudVedSidenAfMaalXpath = "//h4[text()='Ved siden af mål']/following-sibling::div";

        String team1SkudXpath = "//b[contains(@class, 'ml-ProgressBar_MiniBarValue-1')]";
        String team2SkudXpath = "//b[contains(@class, 'ml-ProgressBar_MiniBarValue-2')]";

        int team1SkudPaaMaal = Integer.parseInt(new PageElement(By.xpath(skudWrapperXpath + skudPaaMaalXpath + team1SkudXpath)).getText());
        int team2SkudPaaMaal = Integer.parseInt(new PageElement(By.xpath(skudWrapperXpath + skudPaaMaalXpath + team2SkudXpath)).getText());

        int team1SkudVedSidenAfMaal = Integer.parseInt(new PageElement(By.xpath(skudWrapperXpath + skudVedSidenAfMaalXpath + team1SkudXpath)).getText());
        int team2SkudVedSidenAfMaal = Integer.parseInt(new PageElement(By.xpath(skudWrapperXpath + skudVedSidenAfMaalXpath + team2SkudXpath)).getText());

        TeamStats team1 = new TeamStats(team1Angreb, team1FarligeAngreb, team1Besiddelse, team1SkudPaaMaal, team1SkudVedSidenAfMaal, score.get(0));
        TeamStats team2 = new TeamStats(team2Angreb, team2FarligeAngreb, team2Besiddelse, team2SkudPaaMaal, team2SkudVedSidenAfMaal, score.get(1));

        return new FixtureStatistics(team1, team2);
    }

    private boolean hasStats(Fixture fixture) {
        PageElement container = fixture.getContainer();
        if (!container.isPresent()) {
            System.out.println("No stats");
            return false;
        }
        try {
            return !container
                    .findElements(By.xpath("./following-sibling::div[@class='ovm-MediaIconContainer ']//div[contains(@class, 'me-MediaButtonLoader')]"))
                    .isEmpty();
        } catch (Exception e) {
            System.out.println("Error getting stats");
            return false;
        }
    }

    public Bet365LiveOddsPage activateStats(Fixture fixture) {
        try {
            PageElement container = fixture.getContainer();
            if (!container.isPresent()) {
                System.out.println("Unable to find container");
                return null;
            }

            try {
                container.findElement(By.xpath("./following-sibling::div[@class='ovm-MediaIconContainer ']//div[contains(@class, 'me-MediaButtonLoader')]")).click();
            } catch (ElementNotInteractableException | StaleElementReferenceException e) {
                System.out.println("Unable to click container");
                return null;
            }

        } catch (TimeoutException e) {
            System.out.println("Unable to get stats");
            return null;
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
            System.out.printf("Failed with: %s - %s%n", fixture.getTeam1Name(), fixture.getTeam2Name() );
            return null;
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

    public boolean isLoading() {
        PageElement loading = new PageElement(By.xpath("//*[@class='bl-Preloader']"));
        return loading.isPresent();
    }
}
