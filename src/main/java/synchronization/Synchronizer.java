package synchronization;

import driver.Engine;
import driver.PatientWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import utils.WaitUtils;

import java.time.Duration;
import java.util.function.Function;

public class Synchronizer {
    private Function<PatientWebDriver, Boolean> sync;

    public Synchronizer(Function<PatientWebDriver, Boolean> sync) {
        this.sync = sync;
    }

    public boolean isSynchronized() {
        Runnable runner = WaitUtils::waitForPageToLoad;

        runner.run();
        new FluentWait<>(Engine.getDriver())
                .pollingEvery(Duration.ofMillis(Engine.getPolling()))
                .withTimeout(Duration.ofMillis(Engine.getTimeout()))
                .until(getSync());
        runner.run();

        return true;
    }

    private Function<PatientWebDriver, Boolean> getSync() {
        return sync;
    }

    public Synchronizer and(Function<PatientWebDriver, Boolean> syncToAdd) {
        this.sync = SynchronizerCombiner.combine(this.sync, syncToAdd);
        return this;
    }
}
