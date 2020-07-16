/*-
 * #%L
 * anchor-plot
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
