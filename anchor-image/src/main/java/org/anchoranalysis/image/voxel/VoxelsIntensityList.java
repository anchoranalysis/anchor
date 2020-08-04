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

package org.anchoranalysis.image.voxel;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.anchoranalysis.image.convert.ByteConverter;

/** A list of intensity values of voxels */
public final class VoxelsIntensityList {

    private DoubleArrayList list;

    public VoxelsIntensityList() {
        list = new DoubleArrayList();
    }

    public VoxelsIntensityList(int initialCapacity) {
        list = new DoubleArrayList(initialCapacity);
    }

    public final void add(double val) {
        list.add(val);
    }

    public final void add(byte val) {
        list.add(ByteConverter.unsignedByteToInt(val));
    }

    public final double sum() {
        return Descriptive.sum(list);
    }

    public final double mean() {
        return Descriptive.mean(list);
    }

    public final double variance(double mean) {
        int size = list.size();
        double sum = mean * size;
        return Descriptive.variance(size, sum, sumOfSquares());
    }

    public final long sumOfSquares() {
        return (long) Descriptive.sumOfSquares(list);
    }

    public final double standardDeviation(double variance) {

        return Descriptive.standardDeviation(variance);
    }

    public int size() {
        return list.size();
    }

    public double get(int arg0) {
        return list.get(arg0);
    }

    public void addAllOf(VoxelsIntensityList arg0) {
        list.addAllOf(arg0.list);
    }
}
