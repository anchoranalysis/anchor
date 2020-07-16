/* (C)2020 */
package org.anchoranalysis.anchor.plot.bean;

import java.util.Iterator;
import java.util.Optional;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;

/**
 * @author Owen Feehan
 * @param <T> item-type
 */
public abstract class GraphDefinition<T> extends AnchorBean<GraphDefinition<T>> {

    /**
     * Creates a graph
     *
     * @param items the items which determine the graph contents
     * @param domainLimits limits on X axis
     * @param rangeLimits limits on Y axis or (empty() and then they are guessed automatically)
     * @return
     * @throws CreateException
     */
    public abstract GraphInstance create(
            Iterator<T> items, Optional<AxisLimits> domainLimits, Optional<AxisLimits> rangeLimits)
            throws CreateException;

    public abstract boolean isItemAccepted(T item);

    public abstract String getTitle();

    public abstract String getShortTitle();
}
