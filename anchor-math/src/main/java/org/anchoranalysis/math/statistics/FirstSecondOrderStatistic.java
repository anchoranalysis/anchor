/* (C)2020 */
package org.anchoranalysis.math.statistics;

public class FirstSecondOrderStatistic {

    /** Mean */
    private double mean;

    /** Standard-Deviation */
    private double scale;

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double zScore(double val) {
        return calcZScore(val, mean, scale);
    }

    public static double calcZScore(double val, double mean, double scale) {
        return (val - mean) / scale;
    }
}
