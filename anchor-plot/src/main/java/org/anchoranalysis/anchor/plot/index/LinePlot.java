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
/* (C)2020 */
package org.anchoranalysis.anchor.plot.index;

import java.util.Iterator;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.IIndexGetter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * A line graph with 1 or more series, with an index on the x-axis and a container with items from
 * which lines are calculated
 *
 * @param <T> container item type
 */
public class LinePlot<T extends IIndexGetter> extends GraphIndexBase<T, XYDataset> {

    @Getter @Setter private int numPoints = 1000;

    @Getter @Setter private int minMaxIgnoreBeforeIndex = 0;

    @Getter @Setter private boolean ignoreRangeAxisOutside = false;

    @Getter @Setter private YValGetter<T> yValGetter;

    @Getter @Setter private double yAxisUpperMargin = 0;

    @Getter @Setter private double yAxisLowerMargin = 0;

    private AxisLimits domainAxisLimits;
    private AxisLimits rangeAxisLimits;

    /**
     * @author Owen Feehan
     * @param <T> container item-type
     */
    @FunctionalInterface
    public interface YValGetter<T> {
        double getYVal(T item, int yIndex) throws GetOperationFailedException;
    }

    public LinePlot(String graphName, String[] seriesNames, YValGetter<T> yValGetter) {
        super(graphName, seriesNames);
        this.yValGetter = yValGetter;
    }

    @Override
    protected XYDataset createDataset(Iterator<T> itr) throws GetOperationFailedException {

        XYSeries[] seriesArr = createXYSeriesArray();

        while (itr.hasNext()) {
            T item = itr.next();

            for (int s = 0; s < getNumSeries(); s++) {
                double yVal = yValGetter.getYVal(item, s);
                seriesArr[s].add(item.getIndex(), yVal);

                if (!ignoreRangeAxisOutside && item.getIndex() >= minMaxIgnoreBeforeIndex) {
                    rangeAxisLimits.addIgnoreInfinity(yVal);
                }
            }

            domainAxisLimits.addIgnoreInfinity(item.getIndex());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();

        for (int s = 0; s < getNumSeries(); s++) {
            dataset.addSeries(seriesArr[s]);
        }

        return dataset;
    }

    /** Creates a chart */
    @Override
    protected JFreeChart createChart(
            XYDataset dataset, String title, Optional<AxisLimits> rangeLimits) {

        // create the chart...
        final JFreeChart chart =
                ChartFactory.createXYLineChart(
                        title, // chart title
                        getLabels().getX(), // x axis label
                        getLabels().getY(), // y axis label
                        dataset, // data
                        PlotOrientation.VERTICAL,
                        multipleSeries(), // include legend
                        true, // tooltips
                        false // urls
                        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        getGraphColorScheme().colorChart(chart);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        getGraphColorScheme().colorPlot(plot);

        if (chart.getLegend() != null) {
            getGraphColorScheme().colorLegend(chart.getLegend());
        }

        plot.setDomainCrosshairVisible(true);
        plot.setDomainCrosshairLockedOnData(false);
        plot.setRangeCrosshairVisible(false);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        for (int s = 0; s < getNumSeries(); s++) {
            renderer.setSeriesShapesVisible(s, false);
            renderer.setSeriesShapesVisible(s, false);
            renderer.setSeriesShapesVisible(s, false);
        }

        plot.setRenderer(renderer);

        final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setLowerBound(domainAxisLimits.getAxisMin());
        domainAxis.setUpperBound(domainAxisLimits.getAxisMax());
        getGraphColorScheme().colorAxis(domainAxis);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLowerBound(rangeAxisLimits.getAxisMin() - yAxisLowerMargin);
        rangeAxis.setUpperBound(rangeAxisLimits.getAxisMax() + yAxisUpperMargin);
        getGraphColorScheme().colorAxis(rangeAxis);

        return chart;
    }

    public GraphInstance create(
            Iterator<T> itr, Optional<AxisLimits> domainLimits, Optional<AxisLimits> rangeLimits)
            throws CreateException {

        // Let's setup our limits before we do the creation
        this.domainAxisLimits = duplicateOrCreate(domainLimits);
        this.rangeAxisLimits = duplicateOrCreate(rangeLimits);

        return super.createWithRangeLimits(itr, rangeLimits);
    }

    public void setYAxisMargins(double margins) {
        setYAxisUpperMargin(margins);
        setYAxisLowerMargin(margins);
    }

    @Override
    protected Optional<AxisLimits> rangeLimitsIfEmpty(XYDataset dataset) {
        return Optional.empty();
    }

    private XYSeries[] createXYSeriesArray() {
        XYSeries[] arr = new XYSeries[getNumSeries()];
        for (int s = 0; s < getNumSeries(); s++) {
            arr[s] = new XYSeries(getSeriesNameFor(s));
        }
        return arr;
    }

    private static AxisLimits duplicateOrCreate(Optional<AxisLimits> limits) {
        return limits.map(AxisLimits::duplicate).orElse(new AxisLimits());
    }
}
