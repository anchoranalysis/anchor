/* (C)2020 */
package org.anchoranalysis.anchor.plot;

import java.util.Optional;
import org.jfree.chart.JFreeChart;

public class GraphInstance {

    private JFreeChart chart;
    private boolean showVerticalAxisLines = true;

    private Optional<AxisLimits> rangeAxisLimits;

    public GraphInstance(JFreeChart chart, Optional<AxisLimits> rangeAxisLimits) {
        super();
        this.chart = chart;
        this.rangeAxisLimits = rangeAxisLimits;
    }

    public JFreeChart getChart() {
        return chart;
    }

    public void setChart(JFreeChart chart) {
        this.chart = chart;
    }

    public boolean isShowVerticalAxisLines() {
        return showVerticalAxisLines;
    }

    public void setShowVerticalAxisLines(boolean showVerticalAxisLines) {
        this.showVerticalAxisLines = showVerticalAxisLines;
    }

    public Optional<AxisLimits> getRangeAxisLimits() {
        return rangeAxisLimits;
    }

    public void setRangeAxisLimits(Optional<AxisLimits> rangeAxisLimits) {
        this.rangeAxisLimits = rangeAxisLimits;
    }
}
