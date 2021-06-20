package model;

public class TeamStats {
    private final Integer angreb;
    private final Integer farligeAngreb;
    private final Integer besiddelse;
    private final int skudPaaMaal;
    private final int skudVedSidenAfMaal;
    private final int maal;

    public TeamStats(Integer angreb, Integer farligeAngreb, Integer besiddelse, int skudPaaMaal, int skudVedSidenAfMaal, int maal) {
        this.angreb = angreb;
        this.farligeAngreb = farligeAngreb;
        this.besiddelse = besiddelse;
        this.skudPaaMaal = skudPaaMaal;
        this.skudVedSidenAfMaal = skudVedSidenAfMaal;
        this.maal = maal;
    }

    public Integer getAngreb() {
        return angreb;
    }

    public Integer getFarligeAngreb() {
        return farligeAngreb;
    }

    public Integer getBesiddelse() {
        return besiddelse;
    }

    public int getSkudPaaMaal() {
        return skudPaaMaal;
    }

    public int getSkudVedSidenAfMaal() {
        return skudVedSidenAfMaal;
    }

    public int getMaal() {
        return maal;
    }

    @Override
    public String toString() {
        return "TeamStats{" +
                "angreb=" + angreb +
                ", farligeAngreb=" + farligeAngreb +
                ", besiddelse=" + besiddelse +
                ", skudPaaMaal=" + skudPaaMaal +
                ", skudVedSidenAfMaal=" + skudVedSidenAfMaal +
                ", maal=" + maal +
                '}';
    }
}
