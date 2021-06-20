import login.Login;
import model.Fixture;
import model.FixtureStatistics;
import model.Pair;
import org.testng.annotations.Test;
import pageobjects.bet365.Bet365LiveOddsPage;
import pageobjects.bet365.Bet365StartPage;
import utils.PostUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.PostUtils.postMatch;

public class Bet365Test {

    @Test
    public void login() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, KeyManagementException, NoSuchAlgorithmException {

        PostUtils.trust();

        Bet365LiveOddsPage bet365LiveOddsPageClass = bet365Login();
        while (true) {
            Map<String, Fixture> fixturesMap = bet365LiveOddsPageClass.getFixtures();

            List<Fixture> fixturesList = new ArrayList<>(fixturesMap.values());

            final List<Pair<Fixture, FixtureStatistics>> fixtureStatsPairs = new ArrayList<>();
            fixturesList.forEach(f -> fixtureStatsPairs.add(new Pair<>(f, bet365LiveOddsPageClass.getFixtureStats(f))));

            fixtureStatsPairs.forEach(pair -> {
                Fixture l = pair.getL();
                if (pair.getR() != null
                        && !l.getTeam1Name().toLowerCase().contains("esport")
                        && !l.getTeam2Name().toLowerCase().contains("esport")) {
                    try {
                        System.out.println("posting");
                        postMatch(pair);
                        System.out.println("posting ended");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            if(bet365LiveOddsPageClass.isLoading()) {
                bet365Login();
            }
        }
    }

    @Test
    public void login2() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, KeyManagementException, NoSuchAlgorithmException {
        PostUtils.trust();

        Bet365LiveOddsPage bet365LiveOddsPageClass = bet365Login();
        while (true) {
            try {
                bet365LiveOddsPageClass = bet365LiveOddsPageClass.postFixturesAndStats();
            } catch (Exception e)  {
                e.printStackTrace();
            }

            if(bet365LiveOddsPageClass.isLoading()) {
                bet365Login();
            }
        }
    }

    private Bet365LiveOddsPage bet365Login() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        for (int i = 0; i <= 10; i++) {
            try {
                Bet365StartPage page = Login.bet365();
                return page.activateLiveOdds();
            } catch (Exception e) {
                e.printStackTrace();
                if (i == 10) {
                    throw e;
                }
            }
        }
        return null;
    }
}
