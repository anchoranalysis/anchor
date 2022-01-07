/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.threshold;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.NullParametersBean;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.math.histogram.Histogram;

/**
 * Calculates a threshold-level from a histogram.
 *
 * <p>A well-behaved {@link CalculateLevel} should implements {@link #equals} and {@link #hashCode}.
 * If this is not possible, these methods should instead throw a run-time exception.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CalculateLevel extends NullParametersBean<CalculateLevel> {

    /**
     * Determines a voxel intensity that can be used for thresholding.
     *
     * @param histogram a histogram of voxel-intensities from which a threshold-level can be
     *     derived.
     * @return the threshold-level.
     * @throws OperationFailedException if a level cannot be successfully calculated.
     */
    public abstract int calculateLevel(Histogram histogram) throws OperationFailedException;

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
