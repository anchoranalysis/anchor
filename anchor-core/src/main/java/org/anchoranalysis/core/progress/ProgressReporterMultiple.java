/* (C)2020 */
package org.anchoranalysis.core.progress;

// Combines a number of sub progress reporters
public class ProgressReporterMultiple implements AutoCloseable {

    private ProgressReporter progressReporterParent;
    private double part = 0.0;
    private double cumPart = 0.0;
    private int index = 0;

    public ProgressReporterMultiple(ProgressReporter progressReporterParent, int numChildren) {
        this.progressReporterParent = progressReporterParent;
        this.part = 100.0 / numChildren;
        progressReporterParent.setMin(0);
        progressReporterParent.setMax(100);
    }

    public void incrWorker() {
        index++;
        this.cumPart = part * index;
        progressReporterParent.update((int) Math.floor(cumPart));
    }

    // Progress for the current worker
    public void update(double progress) {
        progressReporterParent.update((int) Math.floor(cumPart + (progress * part / 100)));
    }

    @Override
    public void close() {
        progressReporterParent.update(100);
    }
}
