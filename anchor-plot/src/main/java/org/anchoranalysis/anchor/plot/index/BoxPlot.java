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

import java.awt.Paint;
import java.util.Optional;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.GetForSeries;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

/** @param <T> container item type */
public class BoxPlot<T> extends GraphIndexBaseCategorical<T, DefaultBoxAndWhiskerCategoryDataset> {

    private GetForSeries<T, BoxAndWhiskerItem> boxAndWhiskerItemGetter;

    // colorGetter can be NULL to indicate that we do not use custom colors
    public BoxPlot(
            String graphName,
            String[] seriesNames,
            GetForSeries<T, String> labelGetter,
            GetForSeries<T, BoxAndWhiskerItem> boxAndWhiskerItemGetter,
            GetForSeries<T, Paint> colorGetter) {

        super(graphName, seriesNames, labelGetter, colorGetter);

        this.boxAndWhiskerItemGetter = boxAndWhiskerItemGetter;
    }

    @Override
    protected void addLabelToDataset(
            DefaultBoxAndWhiskerCategoryDataset dataset,
            T item,
            int index,
            String seriesName,
            String label)
            throws GetOperationFailedException {
        BoxAndWhiskerItem yVal = boxAndWhiskerItemGetter.get(item, index);
        dataset.add(yVal, seriesName, label);
    }

    private JFreeChart createInitialChartObject(CategoryDataset dataset, String title) {

        final CategoryAxis xAxis = new CategoryAxis(getLabels().getX());
        final NumberAxis yAxis = new NumberAxis(getLabels().getY());
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(true);
        renderer.setMeanVisible(false);

        return new JFreeChart(title, new CategoryPlot(dataset, xAxis, yAxis, renderer));
    }

    @Override
    protected JFreeChart createChart(
            DefaultBoxAndWhiskerCategoryDataset dataset,
            String title,
            Optional<AxisLimits> rangeLimits) {

        final JFreeChart chart = createInitialChartObject(dataset, title);

        getGraphColorScheme().colorChart(chart);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        // -0.5 to 1.0
        if (rangeLimits.isPresent()) {
            rangeAxis.setLowerBound(rangeLimits.get().getAxisMin());
            rangeAxis.setUpperBound(rangeLimits.get().getAxisMax());
        }
        getGraphColorScheme().colorAxis(rangeAxis);

        return chart;
    }

    @Override
    protected Optional<AxisLimits> rangeLimitsIfEmpty(DefaultBoxAndWhiskerCategoryDataset dataset) {
        return Optional.empty();
    }

    @Override
    protected DefaultBoxAndWhiskerCategoryDataset createDefaultDataset() {
        return new DefaultBoxAndWhiskerCategoryDataset();
    }
}
