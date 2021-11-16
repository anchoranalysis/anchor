/*-
 * #%L
 * anchor-feature-io
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.feature.io.results;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.feature.io.csv.RowLabels;
import org.anchoranalysis.feature.io.name.MultiName;
import org.anchoranalysis.feature.results.ResultsVector;

/**
 * Like a {@link ResultsVector} but additionally contains labels to describe the calculated results.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class LabelledResultsVector {

    /** The labels. */
    private final RowLabels labels;

    /** The results. */
    private final ResultsVector results;

    /**
     * Creates with no additional labels, and no group.
     *
     * @param results the results.
     */
    public LabelledResultsVector(ResultsVector results) {
        this(Optional.empty(), results);
    }

    /**
     * Creates with no additional labels other than a group.
     *
     * @param group the associated group.
     * @param results the results.
     */
    public LabelledResultsVector(Optional<MultiName> group, ResultsVector results) {
        this(new RowLabels(Optional.empty(), group), results);
    }

    /**
     * The result of a feature-calculation stored at a particular {@code index}.
     *
     * @param index the index (zero-indexed). It should be {@code >= 0} and {@code < size()}.
     * @return the value corresponding to the feature-calculation or {@link Double#NaN} if an
     *     exception occurred during calculation.
     */
    public double get(int index) {
        return results.get(index);
    }

    /**
     * The number of calculations stored in the vector.
     *
     * @return the total number of calculations in the vector.
     */
    public int size() {
        return results.size();
    }
}
