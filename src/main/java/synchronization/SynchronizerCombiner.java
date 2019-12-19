package synchronization;

import driver.PatientWebDriver;

import java.util.function.Function;

class SynchronizerCombiner {

    @SafeVarargs
    static Function<PatientWebDriver, Boolean> combine(Function<PatientWebDriver, Boolean>... syncs) {
        return patientWebDriver -> {
            for (Function<PatientWebDriver, Boolean> sync : syncs) {
                if (!sync.apply(patientWebDriver)) {
                    return false;
                }
            }
            return true;
        };
    }
}
