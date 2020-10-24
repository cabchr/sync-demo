package model;

import org.openqa.selenium.WebElement;

public class Fixture {
    private final Time time;
    private final String team1Name;
    private final String team2Name;
    private final Double team1winOdds;
    private final Double drawOdds;
    private final Double team2winOdds;
    private final WebElement container;

    public Fixture(Time time, String team1Name, String team2Name, Double team1winOdds, Double drawOdds, Double team2winOdds, WebElement container) {
        this.time = time;
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.team1winOdds = team1winOdds;
        this.drawOdds = drawOdds;
        this.team2winOdds = team2winOdds;
        this.container = container;
    }

    public WebElement getContainer() {
        return container;
    }

    public Time getTime() {
        return time;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public String getTeam2Name() {
        return team2Name;
    }

    public double getTeam1winOdds() {
        return team1winOdds;
    }

    public double getDrawOdds() {
        return drawOdds;
    }

    public double getTeam2winOdds() {
        return team2winOdds;
    }

    @Override
    public String toString() {
        return "Fixture{" +
                "time=" + time +
                ", team1Name='" + team1Name + '\'' +
                ", team2Name='" + team2Name + '\'' +
                ", team1winOdds=" + team1winOdds +
                ", drawOdds=" + drawOdds +
                ", team2winOdds=" + team2winOdds +
                ", container=" + container +
                '}';
    }
}
