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

import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.math.histogram.Histogram;
import org.anchoranalysis.math.statistics.VarianceCalculatorLong;

/**
 * Allows retrieval of statistics about voxel values.
 *
 * @author Owen Feehan
 */
public interface VoxelStatistics {

    /**
     * The total count.
     *
     * @return the total number of voxels.
     */
    long size();

    /**
     * The sum of all voxel values.
     *
     * @return the sum.
     */
    long sum();

    /**
     * The sum of the square of all voxel values.
     *
     * @return the sum-of-squares.
     */
    long sumOfSquares();

    /**
     * Derives statistics only of the voxels that satisfy a condition, relative to a threshold.
     *
     * @param relationToThreshold the condition that must be satisfied for voxels to be included in
     *     the statistics.
     * @return a new {@link VoxelStatistics} pertaining only to the voxels that satisfy the
     *     condition.
     */
    VoxelStatistics threshold(RelationToThreshold relationToThreshold);

    /**
     * The voxel value corresponding to a particular quantile.
     *
     * @param quantile the quantile, which should always be {@code >= 0} and {@code <= 1}.
     * @return the voxel value corresponding to the quantile.
     * @throws OperationFailedException if {@code quantile} is out-of-range.
     * @throws UnsupportedOperationException if the operation is unsupported.
     */
    double quantile(double quantile) throws OperationFailedException;

    /***
     * A {@link Histogram} of all voxel values.
     *
     * @return a histogram.
     * @throws OperationFailedException if a histogram cannot be created.
     */
    Histogram histogram() throws OperationFailedException;

    /**
     * Counts the number of voxels that exist, relative to a threshold.
     *
     * <p>This methods conveniently avoids overhead with assigning new memory when counting.
     *
     * @param relationToThreshold defines the relation to a threshold e.g. above a number, or below
     *     a number.
     * @return the total number of voxels that fulfill {@code relationToThreshold}.
     */
    long countThreshold(RelationToThreshold relationToThreshold);

    /**
     * The mean of all voxel values.
     *
     * @return the mean.
     */
    default double mean() {
        return ((double) sum()) / size();
    }

    /**
     * The variance of all voxel values.
     *
     * @return the variance.
     */
    default double variance() {
        return new VarianceCalculatorLong(sum(), sumOfSquares(), size()).variance();
    }

    /**
     * The standard-deviation of all voxel values.
     *
     * @return the standard-deviation.
     */
    default double stdDev() {
        return Math.sqrt(variance());
    }
}
