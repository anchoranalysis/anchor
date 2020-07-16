/* (C)2020 */
package org.anchoranalysis.anchor.mpp.plot.bean;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.anchor.mpp.plot.execution.ExecutionTimeItem;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.GraphInstance;
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
    public GraphInstance create(
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
