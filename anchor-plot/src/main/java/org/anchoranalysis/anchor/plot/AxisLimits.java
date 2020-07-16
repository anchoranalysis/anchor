/* (C)2020 */
package org.anchoranalysis.anchor.plot;

public class AxisLimits {

    // Inclusive
    private double axisMin = Double.POSITIVE_INFINITY;

    // Inclusive
    private double axisMax = Double.NEGATIVE_INFINITY;

    public AxisLimits() {}

    public AxisLimits(double min, double max) {
        this.axisMin = min;
        this.axisMax = max;
    }

    public AxisLimits duplicate() {
        AxisLimits newObj = new AxisLimits();
        newObj.axisMin = this.axisMin;
        newObj.axisMax = this.axisMax;
        return newObj;
    }

    public double getAxisMin() {
        return axisMin;
    }

    public void setAxisMin(double axisMin) {
        this.axisMin = axisMin;
    }

    public void setAxisMin(int axisMin) {
        this.axisMin = axisMin;
    }

    public double getAxisMax() {
        return axisMax;
    }

    public void setAxisMax(double axisMax) {
        this.axisMax = axisMax;
    }

    public void setAxisMax(int axisMax) {
        this.axisMax = axisMax;
    }

    public void addIgnoreInfinity(double value) {
        if (Double.isFinite(value)) {
            add(value);
        }
    }

    public void add(double value) {
        this.axisMax = Math.max(axisMax, value);
        this.axisMin = Math.min(axisMin, value);
    }
}
