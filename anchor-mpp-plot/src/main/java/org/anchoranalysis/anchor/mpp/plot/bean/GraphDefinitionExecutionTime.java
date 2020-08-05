/*-
 * #%L
 * anchor-mpp-plot
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

package org.anchoranalysis.anchor.mpp.plot.bean;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.anchor.mpp.plot.execution.ExecutionTimeItem;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.PlotInstance;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.index.BarChart;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

public class GraphDefinitionExecutionTime extends GraphDefinition<ExecutionTimeItem> {

    private BarChart<ExecutionTimeItem> delegate;

    public GraphDefinitionExecutionTime() throws InitException {
        delegate =
                new BarChart<>(
                        getTitle(),
                        new String[] {"Execution Time"},
                        (ExecutionTimeItem item, int seriesNum) ->
                                checkFirstSeries(item, seriesNum, ExecutionTimeItem::getObjectID),
                        (ExecutionTimeItem item, int seriesNum) ->
                                checkFirstSeries(
                                        item, seriesNum, a -> (double) a.getExecutionTime()),
                        null,
                        false);
        delegate.getLabels().setX("");
        delegate.getLabels().setY("Execution Time");
    }

    private static <S, T> T checkFirstSeries(S item, int seriesNum, Function<S, T> func) {
        if (seriesNum == 0) {
            return func.apply(item);
        } else {
            throw new AnchorFriendlyRuntimeException(
                    String.format("seriesNum must be 0 but instead it is %d", seriesNum));
        }
    }

    @Override
    public PlotInstance create(
            Iterator<ExecutionTimeItem> items,
            Optional<AxisLimits> domainLimits,
            Optional<AxisLimits> rangeLimits)
            throws CreateException {
        return delegate.createWithRangeLimits(items, rangeLimits);
    }

    @Override
    public boolean isItemAccepted(ExecutionTimeItem item) {
        return true;
    }

    @Override
    public String getTitle() {
        return "Execution Times";
    }

    @Override
    public String getShortTitle() {
        return getTitle();
    }
}
