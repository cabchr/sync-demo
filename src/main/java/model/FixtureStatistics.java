package model;

public class FixtureStatistics {
    private final TeamStats team1;
    private final TeamStats team2;

    public FixtureStatistics(TeamStats team1, TeamStats team2) {
        this.team1 = team1;
        this.team2 = team2;
    }

    @Override
    public String toString() {
        return "FixtureStatistics{" +
                "team1=" + team1 +
                ", team2=" + team2 +
                '}';
    }
}
