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

package org.anchoranalysis.image.voxel.iterator.changed;

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Processes only neighboring voxels that lie on an object-mask.
 *
 * @author Owen Feehan
 * @param <T> result-type that can be collected after processing
 */
final class WithinObjectMask<T> implements ProcessVoxelNeighbor<T> {

    private final ProcessChangedPointAbsoluteMasked<T> delegate;
    private final ObjectMask object;
    private final Extent extent;
    private final ReadableTuple3i cornerMin;

    private Point3i point;
    private Point3i relativeToCorner;

    // Current ByteBuffer for the object-mask
    private UnsignedByteBuffer bufferObject;
    private byte maskOffVal;

    private int maskOffsetXYAtPoint;

    public WithinObjectMask(ProcessChangedPointAbsoluteMasked<T> process, ObjectMask object) {
        this.delegate = process;
        this.object = object;
        this.maskOffVal = object.binaryValuesByte().getOffByte();
        this.extent = object.extent();
        this.cornerMin = object.boundingBox().cornerMin();
    }

    @Override
    public void initSource(Point3i point, int sourceVal, int sourceOffsetXY) {
        this.point = point;

        updateRel(point);
        maskOffsetXYAtPoint = extent.offsetSlice(relativeToCorner);

        delegate.initSource(sourceVal, sourceOffsetXY);
    }

    @Override
    public boolean notifyChangeZ(int zChange) {
        int z1 = point.z() + zChange;

        int relZ1 = relativeToCorner.z() + zChange;

        if (relZ1 < 0 || relZ1 >= extent.z()) {
            this.bufferObject = null;
            return false;
        }

        int zRel = z1 - cornerMin.z();
        this.bufferObject = object.sliceBufferLocal(zRel);

        delegate.notifyChangeZ(zChange, z1, bufferObject);
        return true;
    }

    @Override
    public void processPoint(int xChange, int yChange) {

        int x1 = point.x() + xChange;
        int y1 = point.y() + yChange;

        int relX1 = relativeToCorner.x() + xChange;
        int relY1 = relativeToCorner.y() + yChange;

        if (relX1 < 0) {
            return;
        }

        if (relX1 >= extent.x()) {
            return;
        }

        if (relY1 < 0) {
            return;
        }

        if (relY1 >= extent.y()) {
            return;
        }

        int offset = maskOffsetXYAtPoint + xChange + (yChange * extent.x());

        if (bufferObject.getRaw(offset) != maskOffVal) {
            delegate.processPoint(xChange, yChange, x1, y1, offset);
        }
    }

    @Override
    public T collectResult() {
        return delegate.collectResult();
    }

    private void updateRel(Point3i point) {
        relativeToCorner = Point3i.immutableSubtract(point, cornerMin);
    }
}
