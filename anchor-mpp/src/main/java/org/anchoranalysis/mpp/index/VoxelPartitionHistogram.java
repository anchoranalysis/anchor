/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.index;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.anchoranalysis.math.histogram.Histogram;
import org.anchoranalysis.mpp.index.factory.VoxelPartitionFactory;

public class VoxelPartitionHistogram implements VoxelPartition<Histogram> {

    private Histogram combined;
    private List<Histogram> list;

    public VoxelPartitionHistogram(int numberSlices, Supplier<Histogram> histogramFactory) {

        combined = histogramFactory.get();

        list = new ArrayList<>();
        for (int i = 0; i < numberSlices; i++) {
            list.add(histogramFactory.get());
        }
    }

    @Override
    public Histogram getSlice(int sliceID) {
        return list.get(sliceID);
    }

    @Override
    public void addForSlice(int sliceID, int val) {
        list.get(sliceID).incrementValue(val);
        combined.incrementValue(val);
    }

    @Override
    public Histogram getCombined() {
        return combined;
    }

    @Override
    public void cleanUp(VoxelPartitionFactory<Histogram> factory) {
        factory.addUnused(combined);
        for (int i = 0; i < list.size(); i++) {
            factory.addUnused(list.get(i));
        }
    }

    @Override
    public int numberSlices() {
        return list.size();
    }
}
