import login.Login;
import model.Fixture;
import model.FixtureStatistics;
import model.Pair;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pageobjects.bet365.Bet365LiveOddsPage;
import pageobjects.bet365.Bet365StartPage;
import utils.WaitUtils;
import utils.bet365.FixtureCalculations;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Bet365Test {

    @Test
    public void login() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Bet365StartPage page = Login.bet365();
        Bet365LiveOddsPage bet365LiveOddsPageClass = page.activateLiveOdds(Bet365LiveOddsPage.class);
        while (true) {
            Map<String, Fixture> fixturesMap = bet365LiveOddsPageClass.getFixtures();

            List<Fixture> fixturesList = fixturesMap.values()
                    .stream()
                    .filter(f -> f.getTime().getMinutes() > 8 && f.getTime().getMinutes() < 30)
                    .collect(Collectors.toList());

            final List<Pair<Fixture, FixtureStatistics>> fixtureStatsPairs = new ArrayList<>();
            fixturesList.forEach(f -> fixtureStatsPairs.add(new Pair<>(f, bet365LiveOddsPageClass.getFixtureStats(f))));

            fixtureStatsPairs.forEach(pair -> {
                Fixture l = pair.getL();
                if (pair.getR() != null
                        && !l.getTeam1Name().toLowerCase().contains("esport")
                        && !l.getTeam2Name().toLowerCase().contains("esport")) {
                    FixtureCalculations.apply(pair);
                }
            });

            System.out.println("sleeping....");
            WaitUtils.sleep(20000);
            System.out.println("resuming.... " + LocalDateTime.now().toString());
        }
    }
}
