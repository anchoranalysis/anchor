/* (C)2020 */
package org.anchoranalysis.core.progress;

public class ProgressReporterConsole implements ProgressReporter {

    private int max;
    private int min;

    private int nextPercentageToReport = 0;
    private int incrementSize;

    public ProgressReporterConsole(int incrementSize) {
        super();
        this.incrementSize = incrementSize;
    }

    private double percentCompleted(int val) {
        return ((double) (val - min)) / (max - min) * 100;
    }

    @Override
    public void open() {
        System.out.printf("[ "); // NOSONAR
    }

    private void reportPercentage(int percent) {
        System.out.printf("%d%s ", percent, "%"); // NOSONAR
        nextPercentageToReport += incrementSize;
    }

    @Override
    public void close() {
        if (nextPercentageToReport >= 100) {
            reportPercentage(100);
        }
        System.out.printf("]%n"); // NOSONAR
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public void update(int val) {

        double percent = percentCompleted(val);
        while (percent > nextPercentageToReport) {
            reportPercentage(nextPercentageToReport);
        }
    }

    @Override
    public void setMin(int min) {
        this.min = min;
    }

    @Override
    public void setMax(int max) {
        this.max = max;
    }
}
