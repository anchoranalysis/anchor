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

package org.anchoranalysis.image.voxel.kernel;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.Point3i;

public abstract class Kernel {

    private int size;
    private int sizeHalf;

    // Only use odd sizes
    public Kernel(int size) {
        super();
        this.size = size;
        this.sizeHalf = (size - 1) / 2;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSizeHalf() {
        return sizeHalf;
    }

    public int getYMin(Point3i point) {
        return Math.max(point.y() - getSizeHalf(), 0);
    }

    public int getYMax(Point3i point, Extent extent) {
        return Math.min(point.y() + getSizeHalf(), extent.y() - 1);
    }

    public int getXMin(Point3i point) {
        return Math.max(point.x() - getSizeHalf(), 0);
    }

    public int getXMax(Point3i point, Extent extent) {
        return Math.min(point.x() + getSizeHalf(), extent.x() - 1);
    }

    public abstract void init(Voxels<UnsignedByteBuffer> in);

    public abstract void notifyZChange(LocalSlices inSlices, int z);
}