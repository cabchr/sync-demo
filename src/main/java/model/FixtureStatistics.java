package model;

public class FixtureStatistics {
    private final TeamStats team1;
    private final TeamStats team2;

    public FixtureStatistics(TeamStats team1, TeamStats team2) {
        this.team1 = team1;
        this.team2 = team2;
    }

    public TeamStats getTeam1() {
        return team1;
    }

    public TeamStats getTeam2() {
        return team2;
    }

    @Override
    public String toString() {
        return "FixtureStatistics{" + "\n" +
                "  team1=" + team1 + "\n" +
                "  team2=" + team2 + "\n" +
                '}';
    }
}
