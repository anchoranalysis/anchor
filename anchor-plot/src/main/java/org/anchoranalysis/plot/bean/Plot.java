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

package org.anchoranalysis.plot.bean;

import java.util.Iterator;
import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.plot.AxisLimits;
import org.anchoranalysis.plot.PlotInstance;

/**
 * Defines a plot as a function of data and axis-limits.
 *
 * <p>Every plot describes items of type {@code T} that are variously illustrated on the plot.
 *
 * @author Owen Feehan
 * @param <T> item-type
 */
public abstract class Plot<T> extends AnchorBean<Plot<T>> {

    /**
     * Creates a graph
     *
     * @param items the items which determine the graph contents
     * @param domainLimits limits on X axis
     * @param rangeLimits limits on Y axis or (empty() and then they are guessed automatically)
     * @return a newly created instance of the plot
     * @throws CreateException if anything goes wrong
     */
    public abstract PlotInstance create(
            Iterator<T> items, Optional<AxisLimits> domainLimits, Optional<AxisLimits> rangeLimits)
            throws CreateException;

    /**
     * Whether to include this particular item in the plot?
     *
     * <p>This function is called for all {@code items} passed in {@link #create(Iterator, Optional,
     * Optional)} to filter items that are unwanted or contain undefined or invalid data.
     *
     * @param item the item to test
     * @return true iff the item should be included
     */
    public abstract boolean isItemIncluded(T item);

    /**
     * The full title of the plot.
     *
     * @return the title
     */
    public abstract String getTitle();

    /**
     * A shortened version of {@link #getTitle}.
     *
     * @return the shortened title
     */
    public abstract String getShortTitle();
}
