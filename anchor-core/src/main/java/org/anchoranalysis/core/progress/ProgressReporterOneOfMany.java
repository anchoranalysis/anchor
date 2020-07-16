/* (C)2020 */
package org.anchoranalysis.core.progress;

public class ProgressReporterOneOfMany implements ProgressReporter {

    private int min;
    private int max;
    private ProgressReporterMultiple progressReporterParent;

    private int range;

    public ProgressReporterOneOfMany(ProgressReporterMultiple progressReporterParent) {
        this(progressReporterParent, 0, 1);
    }

    public ProgressReporterOneOfMany(
            ProgressReporterMultiple progressReporterParent, int min, int max) {
        super();
        this.progressReporterParent = progressReporterParent;
        this.min = min;
        this.max = max;
        updateRange();
    }

    private void updateRange() {
        this.range = max - min;
    }

    @Override
    public void open() {
        // NOTHING TO DO
    }

    @Override
    public void update(int val) {

        int valRec = val - min;
        double progress = (((double) valRec) / range) * 100;
        progressReporterParent.update(progress);
    }

    @Override
    public void close() {
        progressReporterParent.update(100.0);
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public void setMin(int min) {
        this.min = min;
        updateRange();
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public void setMax(int max) {
        this.max = max;
        updateRange();
    }
}
