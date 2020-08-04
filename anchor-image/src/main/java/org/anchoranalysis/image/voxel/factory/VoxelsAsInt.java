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

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferInt;
import org.anchoranalysis.image.voxel.pixelsforplane.PixelsForPlane;

final class VoxelsAsInt extends Voxels<IntBuffer> {

    public VoxelsAsInt(PixelsForPlane<IntBuffer> pixelsForPlane) {
        super(pixelsForPlane, VoxelsFactory.getInt());
    }

    @Override
    public int ceilOfMaxPixel() {

        int max = 0;
        boolean first = true;

        for (int z = 0; z < getPlaneAccess().extent().z(); z++) {

            IntBuffer pixels = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (pixels.hasRemaining()) {

                int val = pixels.get();
                if (first || val > max) {
                    max = val;
                    first = false;
                }
            }
        }
        return max;
    }

    @Override
    public void copyItem(IntBuffer srcBuffer, int srcIndex, IntBuffer destBuffer, int destIndex) {
        destBuffer.put(destIndex, srcBuffer.get(srcIndex));
    }

    @Override
    public boolean isGreaterThan(IntBuffer buffer, int operand) {
        return buffer.get() > operand;
    }

    @Override
    public ObjectMask equalMask(BoundingBox box, int equalVal) {

        ObjectMask object = new ObjectMask(box);

        ReadableTuple3i pointMax = box.calcCornerMax();

        byte maskOnVal = object.binaryValuesByte().getOnByte();

        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            IntBuffer pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer pixelOut =
                    object.voxels().slice(z - box.cornerMin().z()).buffer();

            int ind = 0;
            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    int index = getPlaneAccess().extent().offset(x, y);
                    int chnlVal = pixelIn.get(index);

                    if (chnlVal == equalVal) {
                        pixelOut.put(ind, maskOnVal);
                    }

                    ind++;
                }
            }
        }

        return object;
    }

    @Override
    public void setAllPixelsTo(int val) {

        for (int z = 0; z < extent().z(); z++) {

            IntBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                buffer.put(val);
            }
        }
    }

    @Override
    public void setPixelsTo(BoundingBox box, int val) {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calcCornerMax();
        Extent e = extent();

        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            IntBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            for (int y = cornerMin.y(); y <= cornerMax.y(); y++) {
                for (int x = cornerMin.x(); x <= cornerMax.x(); x++) {
                    int offset = e.offset(x, y);
                    buffer.put(offset, val);
                }
            }
        }
    }

    @Override
    public boolean isEqualTo(IntBuffer buffer, int operand) {
        return buffer.get() == operand;
    }

    @Override
    public Voxels<IntBuffer> maxIntensityProjection() {

        MaxIntensityBufferInt mi = new MaxIntensityBufferInt(extent());

        for (int z = 0; z < extent().z(); z++) {
            mi.projectSlice(getPlaneAccess().getPixelsForPlane(z).buffer());
        }

        return mi.getProjection();
    }

    @Override
    public void multiplyBy(double val) {

        if (val == 1) {
            return;
        }

        for (int z = 0; z < extent().z(); z++) {

            IntBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                int mult = (int) (buffer.get() * val);
                buffer.put(buffer.position() - 1, mult);
            }
        }
    }

    @Override
    public void setVoxel(int x, int y, int z, int val) {
        IntBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        buffer.put(getPlaneAccess().extent().offset(x, y), val);
    }

    @Override
    public int getVoxel(int x, int y, int z) {
        IntBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        return buffer.get(getPlaneAccess().extent().offset(x, y));
    }

    // TODO when values are too small or too large
    @Override
    public void addPixelsCheckMask(ObjectMask objectMask, int value) {

        BoundingBox box = objectMask.boundingBox();
        Voxels<ByteBuffer> voxels = objectMask.voxels();

        byte maskOnByte = objectMask.binaryValuesByte().getOnByte();

        ReadableTuple3i pointMax = box.calcCornerMax();
        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            IntBuffer pixels = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer pixelsMask =
                    voxels.slice(z - box.cornerMin().z()).buffer();

            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    int indexMask = getPlaneAccess().extent().offset(x, y);
                    if (pixelsMask.get(indexMask) == maskOnByte) {
                        int index = getPlaneAccess().extent().offset(x, y);

                        int intVal = pixels.get(index) + value;
                        pixels.put(index, intVal);
                    }
                }
            }
        }
    }

    @Override
    public void scalePixelsCheckMask(ObjectMask objectMask, double value) {
        throw new IllegalArgumentException("Currently unsupported method");
    }

    @Override
    public boolean isEqualTo(IntBuffer buffer1, IntBuffer buffer2) {
        return buffer1.get() == buffer2.get();
    }

    @Override
    public void subtractFrom(int val) {

        for (int z = 0; z < extent().z(); z++) {

            IntBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                int newVal = val - buffer.get();
                buffer.put(buffer.position() - 1, newVal);
            }
        }
    }

    @Override
    public void max(Voxels<IntBuffer> other) throws OperationFailedException {
        throw new OperationFailedException("unsupported operation");
    }

    @Override
    public Voxels<IntBuffer> meanIntensityProjection() {
        throw new UnsupportedOperationException();
    }
}
