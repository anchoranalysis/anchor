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

package org.anchoranalysis.anchor.plot.index;

import java.util.Iterator;
import java.util.Optional;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.PlotInstance;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

/**
 * @author Owen Feehan
 * @param <T> container item-type
 * @param <S> dataset-type
 */
public abstract class GraphIndexBase<T, S extends Dataset> {

    private String graphName;
    private String[] seriesNames;

    private int numSeries;

    private GraphColorScheme graphColorScheme = new GraphColorScheme();

    private GraphAxisLabels labels = new GraphAxisLabels();

    /**
     * Constructor
     *
     * @param graphName
     * @param seriesNames
     */
    public GraphIndexBase(String graphName, String[] seriesNames) {

        this.graphName = graphName;
        this.seriesNames = seriesNames;

        numSeries = seriesNames.length;
    }

    public PlotInstance createWithRangeLimits(
            Iterator<T> itr, Optional<AxisLimits> proposedRangeLimits) throws CreateException {

        try {
            if (numSeries <= 0) {
                throw new CreateException("There must be at least one seriesName");
            }

            // This will create the dataset
            S dataset = createDataset(itr);

            // We populate our outgoing limits from the plot
            if (!proposedRangeLimits.isPresent()) {
                proposedRangeLimits = rangeLimitsIfEmpty(dataset);
            }

            // based on the dataset we create the chart
            final JFreeChart chart = createChart(dataset, this.graphName, proposedRangeLimits);

            // For now we don't set any limits for bar graphs
            return new PlotInstance(chart, proposedRangeLimits);
        } catch (GetOperationFailedException e) {
            throw new CreateException(e);
        }
    }

    public GraphColorScheme getGraphColorScheme() {
        return graphColorScheme;
    }

    public void setGraphColorScheme(GraphColorScheme graphColorScheme) {
        this.graphColorScheme = graphColorScheme;
    }

    public GraphAxisLabels getLabels() {
        return labels;
    }

    protected int getNumSeries() {
        return numSeries;
    }

    protected abstract Optional<AxisLimits> rangeLimitsIfEmpty(S dataset);

    protected abstract JFreeChart createChart(
            S dataset, String title, Optional<AxisLimits> rangeLimits);

    protected abstract S createDataset(Iterator<T> itr) throws GetOperationFailedException;

    protected boolean multipleSeries() {
        return numSeries > 1;
    }

    protected String getSeriesNameFor(int index) {
        return seriesNames[index];
    }
}
