/*-
 * #%L
 * anchor-annotation-io
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

package org.anchoranalysis.annotation.io.assignment;

import java.util.List;
import org.anchoranalysis.annotation.io.comparer.StatisticsToExport;

/**
 * A one-to-one pairing between items in two respective sets.
 *
 * <p>Any item used in one pairing, may not be used in another.
 *
 * <p>The two sets are referred to as <i>left</i> and <i>right</i> respectively.
 *
 * <p>The terminology is borrowed from the <a
 * href="https://en.wikipedia.org/wiki/Assignment_problem">the assignment problem</a>.
 *
 * <p>Statistics are also associated with the assignment, giving different measures of how
 * successful the assignment was.
 *
 * @param <T> item-type.
 * @author Owen Feehan
 */
public interface Assignment<T> {

    /**
     * The number of items that are <b>paired</b>.
     *
     * <p>This is necessarily the same number for either set.
     *
     * @return the total number of pairings.
     */
    int numberPaired();

    /**
     * The number of items in a particular set that are <b>not paired</b>.
     *
     * @param left if true, the <i>left</i>-set is considered, otherwise the <i>right</i>-set.
     * @return the number of unpaired items for the respective set.
     */
    int numberUnassigned(boolean left);

    /**
     * The items that are <b>paired</b> for a particular set.
     *
     * @param left if true, the <i>left</i>-set is considered, otherwise the <i>right</i>-set.
     * @return a newly created list containing the paired items.
     */
    List<T> paired(boolean left);

    /**
     * The items that are <b>not paired</b> for a particular set.
     *
     * @param left if true, the <i>left</i>-set is considered, otherwise the <i>right</i>-set.
     * @return a newly created list containing the unpaired items for the respective set.
     */
    List<T> unassigned(boolean left);

    /**
     * The statistics associated with the assignment.
     *
     * @return the statistics.
     */
    public StatisticsToExport statistics();
}
