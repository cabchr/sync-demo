package utils.bet365;

import model.Fixture;
import model.FixtureStatistics;
import model.Pair;

public class FixtureCalculations {
    public static boolean apply(Pair<Fixture, FixtureStatistics> fixtureStatisticsPair) {
        Fixture fixture = fixtureStatisticsPair.getL();
        FixtureStatistics fixtureStats = fixtureStatisticsPair.getR();

        System.out.println(fixture);
        System.out.println(fixtureStats);

        return false;
    }
}
