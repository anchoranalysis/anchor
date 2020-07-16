/* (C)2020 */
package org.anchoranalysis.core.progress;

/**
 * Placeholder that doesn't measure any progress
 *
 * <p>Implemented as a singleton, to avoid repeated instances of creating a new Autocloseable class,
 * which confuses lint tools like SonarQube
 *
 * @author Owen Feehan
 */
public class ProgressReporterNull implements ProgressReporter {

    private static ProgressReporterNull instance = null;

    private ProgressReporterNull() {}

    public static ProgressReporterNull get() {
        if (instance == null) {
            instance = new ProgressReporterNull();
        }
        return instance;
    }

    @Override
    public void open() {
        // DOES NOTHING
    }

    @Override
    public void close() {
        // DOES NOTHING
    }

    @Override
    public int getMax() {
        // Arbitrary value
        return 0;
    }

    @Override
    public int getMin() {
        // Arbitrary value
        return 0;
    }

    @Override
    public void update(int val) {
        // DOES NOTHING
    }

    @Override
    public void setMin(int min) {
        // DOES NOTHING
    }

    @Override
    public void setMax(int max) {
        // DOES NOTHING
    }
}
