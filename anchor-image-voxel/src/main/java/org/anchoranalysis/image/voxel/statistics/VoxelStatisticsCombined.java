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

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.math.histogram.Histogram;

/**
 * Aggregates statistics of a list of {@link VoxelStatistics} as if they were one.
 *
 * @author Owen Feehan
 */
public class VoxelStatisticsCombined implements VoxelStatistics {

    private List<VoxelStatistics> list = new ArrayList<>();

    /**
     * Adds an element.
     *
     * @param element an element to add.
     */
    public void add(VoxelStatistics element) {
        list.add(element);
    }

    @Override
    public long size() {
        int size = 0;
        for (VoxelStatistics stats : list) {
            size += stats.size();
        }
        return size;
    }

    @Override
    public long sum() {
        int sum = 0;
        for (VoxelStatistics stats : list) {
            sum += stats.sum();
        }
        return sum;
    }

    @Override
    public long sumOfSquares() {
        long count = 0;
        for (VoxelStatistics stats : list) {
            count += stats.sumOfSquares();
        }
        return count;
    }

    @Override
    public VoxelStatistics threshold(RelationToThreshold relationToThreshold) {

        VoxelStatisticsCombined out = new VoxelStatisticsCombined();
        for (VoxelStatistics element : list) {
            out.add(element.threshold(relationToThreshold));
        }
        return out;
    }

    @Override
    public long countThreshold(RelationToThreshold relationToThreshold) {
        long count = 0;
        for (VoxelStatistics element : list) {
            count += element.countThreshold(relationToThreshold);
        }
        return count;
    }

    @Override
    public double quantile(double quantile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Histogram histogram() throws OperationFailedException {

        Histogram out = new Histogram(255);
        for (VoxelStatistics element : list) {
            out.addHistogram(element.histogram());
        }
        return out;
    }
}
