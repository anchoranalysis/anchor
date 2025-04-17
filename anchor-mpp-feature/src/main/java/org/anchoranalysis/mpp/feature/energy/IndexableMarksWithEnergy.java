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

package org.anchoranalysis.mpp.feature.energy;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.core.index.Indexable;
import org.anchoranalysis.mpp.feature.energy.marks.MarksWithEnergyBreakdown;

/**
 * An {@link Indexable} object that contains {@link MarksWithEnergyBreakdown}.
 *
 * <p>This class extends {@link Indexable} to provide an index for the marks with energy breakdown,
 * typically used in iterative processes.
 */
@EqualsAndHashCode(callSuper = true)
public class IndexableMarksWithEnergy extends Indexable {

    /** The marks with their energy breakdown. */
    @Getter private MarksWithEnergyBreakdown marks;

    /**
     * Creates a new {@link IndexableMarksWithEnergy}.
     *
     * @param iter the iteration number or index
     * @param marks the {@link MarksWithEnergyBreakdown} associated with this index
     */
    public IndexableMarksWithEnergy(int iter, MarksWithEnergyBreakdown marks) {
        super(iter);
        this.marks = marks;
    }
}
