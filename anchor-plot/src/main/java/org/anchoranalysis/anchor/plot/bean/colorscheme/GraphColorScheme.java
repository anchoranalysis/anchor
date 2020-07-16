/* (C)2020 */
package org.anchoranalysis.anchor.plot.bean.colorscheme;

import java.awt.Color;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.color.RGBColorBean;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;

public class GraphColorScheme extends AnchorBean<GraphColorScheme> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter
    private RGBColorBean backgroundColor = new RGBColorBean(255, 255, 255);

    @BeanField @Getter @Setter private RGBColorBean axisColor = new RGBColorBean(0, 0, 0);

    @BeanField @Getter @Setter
    private RGBColorBean plotBackgroundColor = new RGBColorBean(Color.lightGray);

    @BeanField @Getter @Setter
    private RGBColorBean plotGridlineColor = new RGBColorBean(Color.white);
    // END BEAN PROPERTIES

    public void colorAxis(Axis axis) {
        axis.setLabelPaint(axisColor.toAWTColor());
        axis.setAxisLinePaint(axisColor.toAWTColor());
        axis.setTickLabelPaint(axisColor.toAWTColor());
        axis.setTickMarkPaint(axisColor.toAWTColor());
    }

    public void colorChart(JFreeChart chart) {
        chart.setBackgroundPaint(backgroundColor.toAWTColor());
        chart.getTitle().setPaint(axisColor.toAWTColor());
    }

    public void colorPlot(XYPlot plot) {
        plot.setBackgroundPaint(plotBackgroundColor.toAWTColor());
        plot.setDomainGridlinePaint(plotGridlineColor.toAWTColor());
        plot.setRangeGridlinePaint(plotGridlineColor.toAWTColor());
    }

    public void colorPlot(CategoryPlot plot) {
        plot.setBackgroundPaint(plotBackgroundColor.toAWTColor());
        plot.setDomainGridlinePaint(plotGridlineColor.toAWTColor());
        plot.setRangeGridlinePaint(plotGridlineColor.toAWTColor());
    }

    public void colorLegend(LegendTitle legend) {
        legend.setBackgroundPaint(backgroundColor.toAWTColor());
        legend.setItemPaint(axisColor.toAWTColor());
    }
}
