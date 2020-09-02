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

package org.anchoranalysis.plot.index;

import hep.aida.bin.DynamicBin1D;
import java.awt.Paint;
import java.util.Optional;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.plot.AxisLimits;
import org.anchoranalysis.plot.GetForSeries;
import org.anchoranalysis.plot.bean.colorscheme.GraphColorScheme;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * A line graph with 1 or more series, with an index on the x-axis and a container with items from
 * which lines are calculated
 *
 * @param <T> cntr-item-type
 */
public class BarChart<T> extends GraphIndexBaseCategorical<T, DefaultCategoryDataset> {

    private GetForSeries<T, Double> yValGetter;

    private boolean showDomainAxis = true;

    private boolean stacked = false;

    private class CustomRenderer extends BarRenderer {

        /** */
        private static final long serialVersionUID = 6311095590591084228L;

        // Row is the series
        // Column is the category
        @Override
        public Paint getItemPaint(final int row, final int column) {
            return getSeriesColors().get(column);
        }

        // Row is the series
        // Column is the category
        @Override
        public Paint getItemOutlinePaint(final int row, final int column) {
            return getSeriesColors().get(column);
        }
    }

    /**
     * @param graphName
     * @param seriesNames
     * @param labelGetter
     * @param yValGetter
     * @param colorGetter custom color getter or null to indicate we use defaults
     * @param stacked
     */
    public BarChart(
            String graphName,
            String[] seriesNames,
            GetForSeries<T, String> labelGetter,
            GetForSeries<T, Double> yValGetter,
            GetForSeries<T, Paint> colorGetter,
            boolean stacked) {
        super(graphName, seriesNames, labelGetter, colorGetter);

        this.yValGetter = yValGetter;
        this.stacked = stacked;
    }

    @Override
    protected void addLabelToDataset(
            DefaultCategoryDataset dataset, T item, int index, String seriesName, String label)
            throws GetOperationFailedException {
        double yVal = yValGetter.get(item, index);
        dataset.addValue(yVal, seriesName, label);
    }

    /** Creates a chart */
    @Override
    protected JFreeChart createChart(
            DefaultCategoryDataset dataset, String title, Optional<AxisLimits> rangeLimits) {

        GraphColorScheme graphColorScheme = getGraphColorScheme();

        assert (graphColorScheme != null);

        final JFreeChart chart = createInitialChartObject(dataset, title);

        graphColorScheme.colorChart(chart);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        graphColorScheme.colorPlot(plot);

        if (chart.getLegend() != null) {
            graphColorScheme.colorLegend(chart.getLegend());
        }

        if (hasColorGetter()) {
            plot.setRenderer(new CustomRenderer());
        }

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(true);
        renderer.setShadowVisible(false);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        domainAxis.setVisible(showDomainAxis);
        graphColorScheme.colorAxis(domainAxis);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        // -0.5 to 1.0
        if (rangeLimits.isPresent()) {
            rangeAxis.setLowerBound(rangeLimits.get().getAxisMin());
            rangeAxis.setUpperBound(rangeLimits.get().getAxisMax());
        }
        graphColorScheme.colorAxis(rangeAxis);

        return chart;
    }

    @Override
    protected Optional<AxisLimits> rangeLimitsIfEmpty(DefaultCategoryDataset dataset) {
        DynamicBin1D d = new DynamicBin1D();

        for (int x = 0; x < dataset.getColumnCount(); x++) {
            for (int y = 0; y < dataset.getRowCount(); y++) {
                d.add(dataset.getValue(y, x).doubleValue());
            }
        }

        AxisLimits limitsOut = new AxisLimits();
        limitsOut.setAxisMin(d.min());
        limitsOut.setAxisMax(d.max());
        return Optional.of(limitsOut);
    }

    public boolean isShowDomainAxis() {
        return showDomainAxis;
    }

    public void setShowDomainAxis(boolean showDomainAxis) {
        this.showDomainAxis = showDomainAxis;
    }

    public GetForSeries<T, Double> getyValGetter() {
        return yValGetter;
    }

    private JFreeChart createInitialChartObject(CategoryDataset dataset, String title) {

        if (stacked) {
            return ChartFactory.createStackedBarChart(
                    title, // chart title
                    getLabels().getX(), // x axis label
                    getLabels().getY(), // y axis label
                    dataset, // data
                    PlotOrientation.VERTICAL,
                    multipleSeries(), // include legend
                    true, // tooltips
                    false // urls
                    );
        } else {
            return ChartFactory.createBarChart(
                    title, // chart title
                    getLabels().getX(), // x axis label
                    getLabels().getY(), // y axis label
                    dataset, // data
                    PlotOrientation.VERTICAL,
                    multipleSeries(), // include legend
                    true, // tooltips
                    false // urls
                    );
        }
    }

    @Override
    protected DefaultCategoryDataset createDefaultDataset() {
        return new DefaultCategoryDataset();
    }
}
