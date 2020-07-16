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
/* (C)2020 */
package org.anchoranalysis.image.voxel.box;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsForPlane;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferInt;

public final class VoxelBoxInt extends VoxelBox<IntBuffer> {

    public VoxelBoxInt(PixelsForPlane<IntBuffer> pixelsForPlane) {
        super(pixelsForPlane, VoxelBoxFactory.getInt());
    }

    @Override
    public int ceilOfMaxPixel() {

        int max = 0;
        boolean first = true;

        for (int z = 0; z < getPlaneAccess().extent().getZ(); z++) {

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
    public ObjectMask equalMask(BoundingBox bbox, int equalVal) {

        ObjectMask object = new ObjectMask(bbox);

        ReadableTuple3i pointMax = bbox.calcCornerMax();

        byte maskOnVal = object.getBinaryValuesByte().getOnByte();

        for (int z = bbox.cornerMin().getZ(); z <= pointMax.getZ(); z++) {

            IntBuffer pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer pixelOut =
                    object.getVoxelBox().getPixelsForPlane(z - bbox.cornerMin().getZ()).buffer();

            int ind = 0;
            for (int y = bbox.cornerMin().getY(); y <= pointMax.getY(); y++) {
                for (int x = bbox.cornerMin().getX(); x <= pointMax.getX(); x++) {

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

        for (int z = 0; z < extent().getZ(); z++) {

            IntBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                buffer.put(val);
            }
        }
    }

    @Override
    public void setPixelsTo(BoundingBox bbox, int val) {

        ReadableTuple3i cornerMin = bbox.cornerMin();
        ReadableTuple3i cornerMax = bbox.calcCornerMax();
        Extent e = extent();

        for (int z = cornerMin.getZ(); z <= cornerMax.getZ(); z++) {

            IntBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            for (int y = cornerMin.getY(); y <= cornerMax.getY(); y++) {
                for (int x = cornerMin.getX(); x <= cornerMax.getX(); x++) {
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
    public VoxelBox<IntBuffer> maxIntensityProj() {

        MaxIntensityBufferInt mi = new MaxIntensityBufferInt(extent());

        for (int z = 0; z < extent().getZ(); z++) {
            mi.projectSlice(getPlaneAccess().getPixelsForPlane(z).buffer());
        }

        return mi.getProjection();
    }

    @Override
    public void multiplyBy(double val) {

        if (val == 1) {
            return;
        }

        for (int z = 0; z < extent().getZ(); z++) {

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
    public void addPixelsCheckMask(ObjectMask mask, int value) {

        BoundingBox bbox = mask.getBoundingBox();
        VoxelBox<ByteBuffer> objectBuffer = mask.getVoxelBox();

        byte maskOnByte = mask.getBinaryValuesByte().getOnByte();

        ReadableTuple3i pointMax = bbox.calcCornerMax();
        for (int z = bbox.cornerMin().getZ(); z <= pointMax.getZ(); z++) {

            IntBuffer pixels = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer pixelsMask =
                    objectBuffer.getPixelsForPlane(z - bbox.cornerMin().getZ()).buffer();

            for (int y = bbox.cornerMin().getY(); y <= pointMax.getY(); y++) {
                for (int x = bbox.cornerMin().getX(); x <= pointMax.getX(); x++) {

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
    public void scalePixelsCheckMask(ObjectMask mask, double value) {
        throw new IllegalArgumentException("Currently unsupported method");
    }

    @Override
    public boolean isEqualTo(IntBuffer buffer1, IntBuffer buffer2) {
        return buffer1.get() == buffer2.get();
    }

    @Override
    public void subtractFrom(int val) {

        for (int z = 0; z < extent().getZ(); z++) {

            IntBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                int newVal = val - buffer.get();
                buffer.put(buffer.position() - 1, newVal);
            }
        }
    }

    @Override
    public void max(VoxelBox<IntBuffer> other) throws OperationFailedException {
        throw new OperationFailedException("unsupported operation");
    }

    @Override
    public VoxelBox<IntBuffer> meanIntensityProj() {
        throw new UnsupportedOperationException();
    }
}
