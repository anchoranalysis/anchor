/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.voxel.statistics;

import lombok.AllArgsConstructor;
import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.math.histogram.Histogram;

/**
 * Calculates statistics from a {@link Histogram} of voxel intensities.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class VoxelStatisticsFromHistogram implements VoxelStatistics {

    /** The histogram to calculate statistics from. */
    private final Histogram histogram;

    @Override
    public long size() {
        return histogram.getTotalCount();
    }

    @Override
    public long sum() {
        return histogram.calculateSum();
    }

    @Override
    public long sumOfSquares() {
        return histogram.calculateSumSquares();
    }

    @Override
    public VoxelStatistics threshold(RelationToThreshold relationToThreshold) {
        throw new UnsupportedOperationException(
                "The threshold operation is not currently supported");
    }

    @Override
    public long countThreshold(RelationToThreshold relationToThreshold) {
        return histogram.countMatching(relationToThreshold.asPredicateInt());
    }

    @Override
    public double quantile(double quantile) throws OperationFailedException {
        return histogram.quantile(quantile);
    }

    @Override
    public Histogram histogram() {
        return histogram;
    }
}
