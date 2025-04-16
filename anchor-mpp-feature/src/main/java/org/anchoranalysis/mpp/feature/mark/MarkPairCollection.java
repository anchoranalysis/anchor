/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.mpp.feature.mark;

import org.anchoranalysis.mpp.feature.addcriteria.RandomCollectionWithAddCriteria;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.pair.MarkPair;

/**
 * A collection of {@link MarkPair}s with {@link Mark}s as the underlying type.
 *
 * <p>This class extends {@link RandomCollectionWithAddCriteria} to provide a specialized collection
 * for pairs of marks, allowing for random access and specific addition criteria.</p>
 */
public class MarkPairCollection extends RandomCollectionWithAddCriteria<MarkPair<Mark>> {

    /**
     * Creates a new {@link MarkPairCollection}.
     *
     * <p>Initializes the collection with {@link MarkPair} as the underlying class type for the random collection.</p>
     */
    public MarkPairCollection() {
        super(MarkPair.class);
    }

    @Override
    public String describeBean() {
        return getBeanName();
    }
}