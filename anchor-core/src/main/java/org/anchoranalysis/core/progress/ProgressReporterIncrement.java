/* (C)2020 */
package org.anchoranalysis.core.progress;

public class ProgressReporterIncrement implements AutoCloseable {

    private ProgressReporter progressReporter;
    private int cnt = 0;

    public ProgressReporterIncrement(ProgressReporter progressReporter) {
        super();
        this.progressReporter = progressReporter;
    }

    public void open() {
        cnt = progressReporter.getMin();
        progressReporter.open();
    }

    @Override
    public void close() {
        progressReporter.close();
    }

    public void update() {
        progressReporter.update(++cnt);
    }

    public int getMin() {
        return progressReporter.getMin();
    }

    public void setMin(int min) {
        progressReporter.setMin(min);
    }

    public int getMax() {
        return progressReporter.getMax();
    }

    public void setMax(int max) {
        progressReporter.setMax(max);
    }
}
