package pageobjects;

import synchronization.Synchronizer;

public abstract class BasePage {
    public BasePage() {
        ensureSyncronized();
    }

    public void ensureSyncronized() {
        Synchronizer synchronizer = synchronizer();
        if (!synchronizer.isSynchronized()) {
            ensureSyncronized();
        }
    }

    public abstract Synchronizer synchronizer();
}
