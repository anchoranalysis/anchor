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

import java.util.Iterator;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.plot.AxisLimits;
import org.anchoranalysis.plot.PlotInstance;
import org.anchoranalysis.plot.bean.colorscheme.PlotColorScheme;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

/**
 * @author Owen Feehan
 * @param <T> container item-type
 * @param <S> dataset-type
 */
public abstract class PlotIndexBase<T, S extends Dataset> {

    private String graphName;
    private String[] seriesNames;

    @Getter private int numberSeries;

    @Getter @Setter private PlotColorScheme graphColorScheme = new PlotColorScheme();

    @Getter private PlotAxisLabels labels = new PlotAxisLabels();

    /**
     * Constructor
     *
     * @param graphName
     * @param seriesNames
     */
    public PlotIndexBase(String graphName, String[] seriesNames) {

        this.graphName = graphName;
        this.seriesNames = seriesNames;

        numberSeries = seriesNames.length;
    }

    public PlotInstance createWithRangeLimits(
            Iterator<T> items, Optional<AxisLimits> proposedRangeLimits) throws CreateException {

        try {
            if (numberSeries <= 0) {
                throw new CreateException("There must be at least one seriesName");
            }

            // This will create the dataset
            S dataset = createDataset(items);

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

    protected abstract Optional<AxisLimits> rangeLimitsIfEmpty(S dataset);

    protected abstract JFreeChart createChart(
            S dataset, String title, Optional<AxisLimits> rangeLimits);

    protected abstract S createDataset(Iterator<T> itr) throws GetOperationFailedException;

    protected boolean multipleSeries() {
        return numberSeries > 1;
    }

    protected String getSeriesNameFor(int index) {
        return seriesNames[index];
    }
}
