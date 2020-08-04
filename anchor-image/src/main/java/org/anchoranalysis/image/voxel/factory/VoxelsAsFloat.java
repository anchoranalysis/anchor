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

package org.anchoranalysis.image.voxel.factory;

import java.nio.FloatBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferFloat;
import org.anchoranalysis.image.voxel.pixelsforplane.PixelsForPlane;

final class VoxelsAsFloat extends Voxels<FloatBuffer> {

    public VoxelsAsFloat(PixelsForPlane<FloatBuffer> pixelsForPlane) {
        super(pixelsForPlane, VoxelsFactory.getFloat());
    }

    @Override
    public int ceilOfMaxPixel() {

        float max = 0;
        boolean first = true;

        for (int z = 0; z < getPlaneAccess().extent().z(); z++) {

            FloatBuffer pixels = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (pixels.hasRemaining()) {

                float val = pixels.get();
                if (first || val > max) {
                    max = val;
                    first = false;
                }
            }
        }
        return (int) Math.ceil(max);
    }

    @Override
    public void copyItem(
            FloatBuffer srcBuffer, int srcIndex, FloatBuffer destBuffer, int destIndex) {
        destBuffer.put(destIndex, srcBuffer.get(srcIndex));
    }

    @Override
    public boolean isGreaterThan(FloatBuffer buffer, int operand) {
        return buffer.get() > operand;
    }

    @Override
    public boolean isEqualTo(FloatBuffer buffer, int operand) {
        return buffer.get() == operand;
    }

    @Override
    public void setAllPixelsTo(int val) {

        float valFloat = (float) val;

        for (int z = 0; z < extent().z(); z++) {

            FloatBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                buffer.put(valFloat);
            }
        }
    }

    @Override
    public void setPixelsTo(BoundingBox box, int val) {

        float valFloat = (float) val;

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calcCornerMax();
        Extent e = extent();

        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            FloatBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            for (int y = cornerMin.y(); y <= cornerMax.y(); y++) {
                for (int x = cornerMin.x(); x <= cornerMax.x(); x++) {
                    int offset = e.offset(x, y);
                    buffer.put(offset, valFloat);
                }
            }
        }
    }

    @Override
    public void multiplyBy(double val) {

        if (val == 1) {
            return;
        }

        for (int z = 0; z < extent().z(); z++) {

            FloatBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                float mult = (float) (buffer.get() * val);
                buffer.put(buffer.position() - 1, mult);
            }
        }
    }

    @Override
    public Voxels<FloatBuffer> maxIntensityProjection() {
        MaxIntensityBufferFloat mi = new MaxIntensityBufferFloat(extent());

        for (int z = 0; z < extent().z(); z++) {
            mi.projectSlice(getPlaneAccess().getPixelsForPlane(z).buffer());
        }

        return mi.getProjection();
    }

    @Override
    public void setVoxel(int x, int y, int z, int val) {
        FloatBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        buffer.put(getPlaneAccess().extent().offset(x, y), (float) val);
    }

    /** Casts a float to an int */
    @Override
    public int getVoxel(int x, int y, int z) {
        FloatBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        return (int) buffer.get(getPlaneAccess().extent().offset(x, y));
    }

    @Override
    public void addPixelsCheckMask(ObjectMask objectMask, int value) {
        throw new IllegalArgumentException("Currently unsupported method");
    }

    @Override
    public void scalePixelsCheckMask(ObjectMask objectMask, double value) {
        throw new IllegalArgumentException("Currently unsupported method");
    }

    @Override
    public boolean isEqualTo(FloatBuffer buffer1, FloatBuffer buffer2) {
        return buffer1.get() == buffer2.get();
    }

    @Override
    public void subtractFrom(int val) {

        for (int z = 0; z < extent().z(); z++) {

            FloatBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                float newVal = val - buffer.get();
                buffer.put(buffer.position() - 1, newVal);
            }
        }
    }

    @Override
    public void max(Voxels<FloatBuffer> other) throws OperationFailedException {
        throw new OperationFailedException("unsupported operation");
    }

    @Override
    public Voxels<FloatBuffer> meanIntensityProjection() {
        throw new UnsupportedOperationException();
    }
}
