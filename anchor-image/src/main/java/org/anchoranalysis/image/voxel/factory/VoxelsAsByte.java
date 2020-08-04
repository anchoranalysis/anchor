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
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferByte;
import org.anchoranalysis.image.voxel.buffer.mean.MeanIntensityByteBuffer;
import org.anchoranalysis.image.voxel.pixelsforplane.PixelsForPlane;

final class VoxelsAsByte extends Voxels<ByteBuffer> {

    public VoxelsAsByte(PixelsForPlane<ByteBuffer> pixelsForPlane) {
        super(pixelsForPlane, VoxelsFactory.getByte());
    }

    public static int ceilOfMaxPixel(PixelsForPlane<?> planeAccess) {
        int max = 0;
        boolean first = true;

        int sizeXY = planeAccess.extent().volumeXY();
        for (int z = 0; z < planeAccess.extent().z(); z++) {

            VoxelBuffer<?> pixels = planeAccess.getPixelsForPlane(z);

            for (int offset = 0; offset < sizeXY; offset++) {

                int val = pixels.getInt(offset);
                if (first || val > max) {
                    max = val;
                    first = false;
                }
            }
        }
        return max;
    }

    @Override
    public int ceilOfMaxPixel() {
        return ceilOfMaxPixel(getPlaneAccess());
    }

    @Override
    public void copyItem(ByteBuffer srcBuffer, int srcIndex, ByteBuffer destBuffer, int destIndex) {
        destBuffer.put(destIndex, srcBuffer.get(srcIndex));
    }

    @Override
    public boolean isGreaterThan(ByteBuffer buffer, int operand) {
        return ByteConverter.unsignedByteToInt(buffer.get()) > operand;
    }

    @Override
    public ObjectMask equalMask(BoundingBox box, int equalVal) {

        ObjectMask object = new ObjectMask(box);

        ReadableTuple3i pointMax = box.calcCornerMax();

        byte equalValByte = (byte) equalVal;
        byte objectMaskOnVal = object.binaryValuesByte().getOnByte();

        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            ByteBuffer pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer pixelOut =
                    object.voxels().slice(z - box.cornerMin().z()).buffer();

            int ind = 0;
            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    int index = getPlaneAccess().extent().offset(x, y);
                    byte chnlVal = pixelIn.get(index);

                    if (chnlVal == equalValByte) {
                        pixelOut.put(ind, objectMaskOnVal);
                    }

                    ind++;
                }
            }
        }

        return object;
    }

    @Override
    public void setAllPixelsTo(int val) {

        byte valByte = (byte) val;

        for (int z = 0; z < extent().z(); z++) {

            ByteBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                buffer.put(valByte);
            }
        }
    }

    @Override
    public void setPixelsTo(BoundingBox box, int val) {

        byte valByte = (byte) val;

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calcCornerMax();
        Extent e = extent();

        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            ByteBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            for (int y = cornerMin.y(); y <= cornerMax.y(); y++) {
                for (int x = cornerMin.x(); x <= cornerMax.x(); x++) {
                    int offset = e.offset(x, y);
                    buffer.put(offset, valByte);
                }
            }
        }
    }

    @Override
    public boolean isEqualTo(ByteBuffer buffer, int operand) {
        return ByteConverter.unsignedByteToInt(buffer.get()) == operand;
    }

    @Override
    public Voxels<ByteBuffer> maxIntensityProjection() {

        MaxIntensityBufferByte mi = new MaxIntensityBufferByte(extent());

        for (int z = 0; z < extent().z(); z++) {
            mi.projectSlice(getPlaneAccess().getPixelsForPlane(z).buffer());
        }

        return mi.getProjection();
    }

    @Override
    public Voxels<ByteBuffer> meanIntensityProjection() {
        MeanIntensityByteBuffer mi = new MeanIntensityByteBuffer(extent());

        for (int z = 0; z < extent().z(); z++) {
            mi.projectSlice(getPlaneAccess().getPixelsForPlane(z).buffer());
        }

        return mi.getFlatBuffer();
    }

    @Override
    public void multiplyBy(double val) {

        if (val == 1) {
            return;
        }

        for (int z = 0; z < extent().z(); z++) {

            ByteBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                int mult = (int) (ByteConverter.unsignedByteToInt(buffer.get()) * val);
                buffer.put(buffer.position() - 1, (byte) mult);
            }
        }
    }

    @Override
    public void setVoxel(int x, int y, int z, int val) {
        ByteBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        buffer.put(getPlaneAccess().extent().offset(x, y), (byte) val);
    }

    @Override
    public int getVoxel(int x, int y, int z) {
        ByteBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        return ByteConverter.unsignedByteToInt(buffer.get(getPlaneAccess().extent().offset(x, y)));
    }

    @Override
    public void scalePixelsCheckMask(ObjectMask objectMask, double value) {

        BoundingBox box = objectMask.boundingBox();
        Voxels<ByteBuffer> objectBuffer = objectMask.voxels();

        byte maskOnByte = objectMask.binaryValuesByte().getOnByte();

        ReadableTuple3i pointMax = box.calcCornerMax();
        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            ByteBuffer pixels = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer pixelsMask =
                    objectBuffer.slice(z - box.cornerMin().z()).buffer();

            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    if (pixelsMask.get() == maskOnByte) {
                        int index = getPlaneAccess().extent().offset(x, y);

                        int intVal =
                                scaleClipped(
                                        value, ByteConverter.unsignedByteToInt(pixels.get(index)));
                        pixels.put(index, (byte) intVal);
                    }
                }
            }
        }
    }

    @Override
    public void addPixelsCheckMask(ObjectMask objectMask, int value) {

        BoundingBox box = objectMask.boundingBox();
        Voxels<ByteBuffer> objectBuffer = objectMask.voxels();

        byte maskOnByte = objectMask.binaryValuesByte().getOnByte();

        ReadableTuple3i pointMax = box.calcCornerMax();
        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            ByteBuffer pixels = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer pixelsMask =
                    objectBuffer.slice(z - box.cornerMin().z()).buffer();

            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    if (pixelsMask.get() == maskOnByte) {
                        int index = getPlaneAccess().extent().offset(x, y);

                        int intVal =
                                addClipped(
                                        value, ByteConverter.unsignedByteToInt(pixels.get(index)));
                        pixels.put(index, (byte) intVal);
                    }
                }
            }
        }
    }

    @Override
    public boolean isEqualTo(ByteBuffer buffer1, ByteBuffer buffer2) {
        return buffer1.get() == buffer2.get();
    }

    @Override
    public void subtractFrom(int val) {

        for (int z = 0; z < extent().z(); z++) {

            ByteBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                int newVal = val - ByteConverter.unsignedByteToInt(buffer.get());
                buffer.put(buffer.position() - 1, (byte) newVal);
            }
        }
    }

    @Override
    public void max(Voxels<ByteBuffer> other) throws OperationFailedException {

        if (!extent().equals(other.extent())) {
            throw new OperationFailedException("other must have same extent");
        }

        int vol = getPlaneAccess().extent().volumeXY();

        for (int z = 0; z < getPlaneAccess().extent().z(); z++) {

            ByteBuffer buffer1 = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer buffer2 = other.getPlaneAccess().getPixelsForPlane(z).buffer();

            int indx = 0;
            while (indx < vol) {

                int elem1 = ByteConverter.unsignedByteToInt(buffer1.get(indx));
                int elem2 = ByteConverter.unsignedByteToInt(buffer2.get(indx));

                if (elem2 > elem1) {
                    buffer1.put(indx, (byte) elem2);
                }

                indx++;
            }
        }
    }

    private static int scaleClipped(double value, int pixelValue) {
        int intVal = (int) Math.round(value * pixelValue);
        if (intVal < 0) {
            return 0;
        }
        if (intVal > 255) {
            return 255;
        }
        return intVal;
    }

    private static int addClipped(int value, int pixelValue) {
        int intVal = pixelValue + value;
        if (intVal < 0) {
            return 0;
        }
        if (intVal > 255) {
            return 255;
        }
        return intVal;
    }
}
