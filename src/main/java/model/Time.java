package model;

public class Time {
    private final int minutes;
    private final int seconds;

    public Time(int minutes, int seconds) {
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    @Override
    public String toString() {
        return "Time{" +
                "minutes=" + minutes +
                ", seconds=" + seconds +
                '}';
    }
}
