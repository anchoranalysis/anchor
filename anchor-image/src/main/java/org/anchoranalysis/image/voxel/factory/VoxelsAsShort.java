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
import java.nio.ShortBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferShort;
import org.anchoranalysis.image.voxel.buffer.mean.MeanIntensityShortBuffer;
import org.anchoranalysis.image.voxel.pixelsforplane.PixelsForPlane;

final class VoxelsAsShort extends Voxels<ShortBuffer> {

    public VoxelsAsShort(PixelsForPlane<ShortBuffer> pixelsForPlane) {
        super(pixelsForPlane, VoxelsFactory.getShort());
    }

    @Override
    public int ceilOfMaxPixel() {
        return VoxelsAsByte.ceilOfMaxPixel(getPlaneAccess());
    }

    @Override
    public void copyItem(
            ShortBuffer srcBuffer, int srcIndex, ShortBuffer destBuffer, int destIndex) {
        destBuffer.put(destIndex, srcBuffer.get(srcIndex));
    }

    @Override
    public boolean isGreaterThan(ShortBuffer buffer, int operand) {
        return buffer.get() > operand;
    }

    @Override
    public ObjectMask equalMask(BoundingBox box, int equalVal) {

        ObjectMask object = new ObjectMask(box);

        ReadableTuple3i pointMax = box.calcCornerMax();

        byte maskOnVal = object.binaryValuesByte().getOnByte();

        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            ShortBuffer pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer pixelOut =
                    object.voxels().slice(z - box.cornerMin().z()).buffer();

            int ind = 0;
            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    int index = getPlaneAccess().extent().offset(x, y);
                    short chnlVal = pixelIn.get(index);

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

            ShortBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                buffer.put((short) val);
            }
        }
    }

    @Override
    public void setPixelsTo(BoundingBox box, int val) {

        short valShort = (short) val;

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calcCornerMax();
        Extent e = extent();

        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            ShortBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            for (int y = cornerMin.y(); y <= cornerMax.y(); y++) {
                for (int x = cornerMin.x(); x <= cornerMax.x(); x++) {
                    int offset = e.offset(x, y);
                    buffer.put(offset, valShort);
                }
            }
        }
    }

    @Override
    public boolean isEqualTo(ShortBuffer buffer, int operand) {
        return buffer.get() == operand;
    }

    @Override
    public void multiplyBy(double val) {

        if (val == 1) {
            return;
        }

        for (int z = 0; z < extent().z(); z++) {

            ShortBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                int mult = (int) (ByteConverter.unsignedShortToInt(buffer.get()) * val);
                buffer.put(buffer.position() - 1, (short) mult);
            }
        }
    }

    @Override
    public void setVoxel(int x, int y, int z, int val) {
        ShortBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        buffer.put(getPlaneAccess().extent().offset(x, y), (short) val);
    }

    @Override
    public int getVoxel(int x, int y, int z) {
        ShortBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        return ByteConverter.unsignedShortToInt(buffer.get(getPlaneAccess().extent().offset(x, y)));
    }

    // TODO when values are too small or too large
    @Override
    public void addPixelsCheckMask(ObjectMask objectMask, int value) {

        BoundingBox box = objectMask.boundingBox();
        Voxels<ByteBuffer> voxels = objectMask.voxels();

        byte maskOnByte = objectMask.binaryValuesByte().getOnByte();

        ReadableTuple3i pointMax = box.calcCornerMax();
        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            ShortBuffer pixels = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer pixelsMask =
                    voxels.slice(z - box.cornerMin().z()).buffer();

            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    int indexMask = getPlaneAccess().extent().offset(x, y);
                    if (pixelsMask.get(indexMask) == maskOnByte) {
                        int index = getPlaneAccess().extent().offset(x, y);

                        short shortVal = (short) (pixels.get(index) + value);
                        pixels.put(index, shortVal);
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
    public boolean isEqualTo(ShortBuffer buffer1, ShortBuffer buffer2) {
        return buffer1.get() == buffer2.get();
    }

    @Override
    public void subtractFrom(int val) {

        for (int z = 0; z < extent().z(); z++) {

            ShortBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                int newVal = val - buffer.get();
                buffer.put(buffer.position() - 1, (short) newVal);
            }
        }
    }

    @Override
    public void max(Voxels<ShortBuffer> other) throws OperationFailedException {
        throw new OperationFailedException("unsupported operation");
    }

    @Override
    public Voxels<ShortBuffer> maxIntensityProjection() {

        MaxIntensityBufferShort mi = new MaxIntensityBufferShort(extent());

        for (int z = 0; z < extent().z(); z++) {
            mi.projectSlice(getPlaneAccess().getPixelsForPlane(z).buffer());
        }

        return mi.getProjection();
    }

    @Override
    public Voxels<ShortBuffer> meanIntensityProjection() {
        MeanIntensityShortBuffer mi = new MeanIntensityShortBuffer(extent());

        for (int z = 0; z < extent().z(); z++) {
            mi.projectSlice(getPlaneAccess().getPixelsForPlane(z).buffer());
        }

        return mi.getFlatBuffer();
    }
}
