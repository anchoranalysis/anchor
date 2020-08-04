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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.interpolator.InterpolateUtilities;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.image.voxel.pixelsforplane.PixelsForPlane;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A box (3-dimensions) with voxel-data.
 *
 * <p>This class is almost <i>immutable</i> the exception being the buffers containing intensity values which can be modified.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@AllArgsConstructor
public abstract class Voxels<T extends Buffer> {

    @Getter private final PixelsForPlane<T> planeAccess;
    private final VoxelsFactoryTypeBound<T> factory;

    public VoxelDataType dataType() {
        return factory.dataType();
    }

    /**
     * A (sub-)region of the voxels.
     *
     * <p>The region may some smaller portion of the voxels, or the voxels in their entirety.
     *
     * <p>It should <b>never</b> be larger than the voxels.
     *
     * <p>Depending on policy, an the existing box will be reused if possible (if the region
     * requested is equal to the box as a whole), useful to avoid unnecessary new memory allocation.
     *
     * <p>If {@link reuseIfPossible} is FALSE, it is guaranteed that a new voxels will always be
     * created.
     *
     * @param box a bounding-box indicating the regions desired (not be larger than the extent)
     * @param reuseIfPossible if TRUE the existing box will be reused if possible,, otherwise a new
     *     box is always created.
     * @return voxels corresponding to the requested region, either newly-created or reused
     */
    public Voxels<T> region(BoundingBox box, boolean reuseIfPossible) {
        if (reuseIfPossible) {
            return regionAvoidNewIfPossible(box);
        } else {
            return regionAlwaysNew(box);
        }
    }

    public void replaceBy(Voxels<T> voxels) throws IncorrectImageSizeException {

        if (!extent().equals(voxels.extent())) {
            throw new IncorrectImageSizeException("Sizes do not match");
        }

        BoundingBox bbo = new BoundingBox(voxels.extent());
        voxels.copyPixelsTo(bbo, this, bbo);
    }

    /**
     * Copies pixels from this object to another object
     *
     * @param sourceBox relative to the current object
     * @param voxelsDestination
     * @param destBox relative to voxelsDestination
     */
    public void copyPixelsTo(BoundingBox sourceBox, Voxels<T> voxelsDestination, BoundingBox destBox) {

        checkExtentMatch(sourceBox, destBox);

        ReadableTuple3i srcStart = sourceBox.cornerMin();
        ReadableTuple3i srcEnd = sourceBox.calcCornerMax();

        Point3i relPos = destBox.relPosTo(sourceBox);

        for (int z = srcStart.z(); z <= srcEnd.z(); z++) {

            assert (z < extent().z());

            T srcArr = getPlaneAccess().getPixelsForPlane(z).buffer();
            T destArr = voxelsDestination.getPlaneAccess().getPixelsForPlane(z + relPos.z()).buffer();

            for (int y = srcStart.y(); y <= srcEnd.y(); y++) {
                for (int x = srcStart.x(); x <= srcEnd.x(); x++) {

                    int srcIndex = getPlaneAccess().extent().offset(x, y);
                    int destIndex =
                            voxelsDestination.extent().offset(x + relPos.x(), y + relPos.y());

                    copyItem(srcArr, srcIndex, destArr, destIndex);
                }
            }
        }
    }

    // Only copies pixels if part of an object, otherwise we set a null pixel
    public void copyPixelsToCheckMask(
            BoundingBox sourceBox,
            Voxels<T> voxelsDestination,
            BoundingBox destBox,
            Voxels<ByteBuffer> voxelsObject,
            BinaryValuesByte bvb) {

        checkExtentMatch(sourceBox, destBox);

        ReadableTuple3i srcStart = sourceBox.cornerMin();
        ReadableTuple3i srcEnd = sourceBox.calcCornerMax();

        Point3i relPos = destBox.relPosTo(sourceBox);

        for (int z = srcStart.z(); z <= srcEnd.z(); z++) {

            T srcArr = getPlaneAccess().getPixelsForPlane(z).buffer();
            T destArr = voxelsDestination.getPlaneAccess().getPixelsForPlane(z + relPos.z()).buffer();

            ByteBuffer maskBuffer =
                    voxelsObject.slice(z - srcStart.z()).buffer();

            for (int y = srcStart.y(); y <= srcEnd.y(); y++) {
                for (int x = srcStart.x(); x <= srcEnd.x(); x++) {

                    int srcIndex = getPlaneAccess().extent().offset(x, y);
                    int destIndex =
                            voxelsDestination.extent().offset(x + relPos.x(), y + relPos.y());

                    if (maskBuffer.get() == bvb.getOnByte()) {
                        copyItem(srcArr, srcIndex, destArr, destIndex);
                    }
                }
            }
        }
    }

    /**
     * Sets pixels in a box to a particular value if they match an Object-Mask
     *
     * <p>See {@link #setPixelsCheckMask} for details
     *
     * @param object the object-mask to restrict which values in the buffer are written to
     * @param value value to be set in matched pixels
     * @return the number of pixels successfully "set"
     */
    public int setPixelsCheckMask(ObjectMask object, int value) {
        return setPixelsCheckMask(
                object.boundingBox(),
                object.voxels(),
                new BoundingBox(object.boundingBox().extent()),
                value,
                object.binaryValuesByte().getOnByte());
    }

    /**
     * Sets pixels in a box to a particular value if they match an Object-Mask
     *
     * See {@link #setPixelsCheckMask(BoundingBox, Voxels, BoundingBox, int, byte) for details
     *
     * @param object the object-mask to restrict which values in the buffer are written to
     * @param value value to be set in matched pixels
     * @param maskMatchValue what's an "On" value for the mask to match against?
     * @return the number of pixels successfully "set"
     */
    public int setPixelsCheckMask(ObjectMask object, int value, byte maskMatchValue) {
        return setPixelsCheckMask(
                object.boundingBox(),
                object.voxels(),
                new BoundingBox(object.boundingBox().extent()),
                value,
                maskMatchValue);
    }

    /**
     * Sets pixels in a box to a particular value if they match a mask in a bounding-box... with more
     * customization
     *
     * <p>Pixels are unchanged if they do not match the mask
     *
     * <p>Bounding boxes can be used to restrict regions in both the source and destination, but
     * must be equal in volume.
     *
     * @param boxToBeAssigned which part of the buffer to write to
     * @param voxelsMask the byte-buffer for the mask
     * @param boxForVoxels which part of scene the mask belongs to
     * @param value value to be set in matched pixels
     * @param maskMatchValue what's an "On" value for {@code maskVoxels} to match against?
     * @return the number of pixels successfully "set"
     */
    public int setPixelsCheckMask(
            BoundingBox boxToBeAssigned,
            Voxels<ByteBuffer> voxelsMask,
            BoundingBox boxForVoxels,
            int value,
            byte maskMatchValue) {
        checkExtentMatch(boxForVoxels, boxToBeAssigned);

        Extent eIntersectingBox = boxForVoxels.extent();

        Extent eAssignBuffer = this.extent();
        Extent eMaskBuffer = voxelsMask.extent();

        int cnt = 0;

        for (int z = 0; z < eIntersectingBox.z(); z++) {

            VoxelBuffer<?> pixels =
                    getPlaneAccess().getPixelsForPlane(z + boxToBeAssigned.cornerMin().z());
            ByteBuffer pixelsMask =
                    voxelsMask.slice(z + boxForVoxels.cornerMin().z()).buffer();

            for (int y = 0; y < eIntersectingBox.y(); y++) {
                for (int x = 0; x < eIntersectingBox.x(); x++) {

                    int indexMask =
                            eMaskBuffer.offset(
                                    x + boxForVoxels.cornerMin().x(),
                                    y + boxForVoxels.cornerMin().y());

                    if (pixelsMask.get(indexMask) == maskMatchValue) {
                        int indexAssgn =
                                eAssignBuffer.offset(
                                        x + boxToBeAssigned.cornerMin().x(),
                                        y + boxToBeAssigned.cornerMin().y());
                        pixels.putInt(indexAssgn, value);
                        cnt++;
                    }
                }
            }
        }

        return cnt;
    }

    public abstract void addPixelsCheckMask(ObjectMask objectMask, int value);

    public abstract void scalePixelsCheckMask(ObjectMask objectMask, double value);

    public abstract void subtractFrom(int val);

    public ObjectMask equalMask(BoundingBox box, int equalVal) {

        ObjectMask object = new ObjectMask(box);

        ReadableTuple3i pointMax = box.calcCornerMax();

        byte maskOnValue = object.binaryValuesByte().getOnByte();

        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            T pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer pixelOut =
                    object.voxels().slice(z - box.cornerMin().z()).buffer();

            int ind = 0;
            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    int index = getPlaneAccess().extent().offset(x, y);

                    pixelIn.position(index);

                    if (isEqualTo(pixelIn, equalVal)) {
                        pixelOut.put(ind, maskOnValue);
                    }

                    ind++;
                }
            }
        }

        return object;
    }

    public ObjectMask greaterThanMask(BoundingBox box, int equalVal) {

        ObjectMask object = new ObjectMask(box);

        ReadableTuple3i pointMax = box.calcCornerMax();

        byte maskOut = object.binaryValuesByte().getOnByte();

        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            T pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer pixelOut =
                    object.voxels().slice(z - box.cornerMin().z()).buffer();

            int ind = 0;
            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    int index = getPlaneAccess().extent().offset(x, y);

                    pixelIn.position(index);

                    if (isGreaterThan(pixelIn, equalVal)) {
                        pixelOut.put(ind, maskOut);
                    }

                    ind++;
                }
            }
        }

        return object;
    }

    public ObjectMask greaterThanMask(ObjectMask objectMask, int equalVal) {

        ObjectMask out = new ObjectMask(objectMask.boundingBox());

        BoundingBox box = objectMask.boundingBox();
        ReadableTuple3i pointMax = box.calcCornerMax();

        byte maskInVal = objectMask.binaryValuesByte().getOnByte();
        byte maskOutVal = out.binaryValuesByte().getOnByte();

        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            T pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer pixelMaskIn = objectMask.voxels().slice(z).buffer();
            ByteBuffer pixelOut =
                    out.voxels().slice(z - box.cornerMin().z()).buffer();

            int ind = 0;
            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    int index = getPlaneAccess().extent().offset(x, y);

                    pixelIn.position(index);

                    byte maskVal = pixelMaskIn.get(ind);

                    if (maskVal == maskInVal && isGreaterThan(pixelIn, equalVal)) {
                        pixelOut.put(ind, maskOutVal);
                    }

                    ind++;
                }
            }
        }

        return out;
    }

    public int countGreaterThan(int operand) {

        int cnt = 0;

        for (int z = 0; z < getPlaneAccess().extent().z(); z++) {

            T buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
            while (buffer.hasRemaining()) {

                if (isGreaterThan(buffer, operand)) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    public boolean hasGreaterThan(int operand) {

        for (int z = 0; z < getPlaneAccess().extent().z(); z++) {

            T buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
            while (buffer.hasRemaining()) {

                if (isGreaterThan(buffer, operand)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasEqualTo(int operand) {

        for (int z = 0; z < getPlaneAccess().extent().z(); z++) {

            T buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
            while (buffer.hasRemaining()) {

                if (isEqualTo(buffer, operand)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int countEqual(int equalVal) {

        int count = 0;

        for (int z = 0; z < getPlaneAccess().extent().z(); z++) {
            T pixels = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (pixels.hasRemaining()) {
                if (isEqualTo(pixels, equalVal)) {
                    count++;
                }
            }
        }
        return count;
    }

    public int countEqualMask(int equalVal, ObjectMask object) {

        ReadableTuple3i srcStart = object.boundingBox().cornerMin();
        ReadableTuple3i srcEnd = object.boundingBox().calcCornerMax();

        int count = 0;

        byte maskOnVal = object.binaryValuesByte().getOnByte();

        for (int z = srcStart.z(); z <= srcEnd.z(); z++) {

            T srcArr = getPlaneAccess().getPixelsForPlane(z).buffer();
            ByteBuffer maskBuffer =
                    object.voxels().slice(z - srcStart.z()).buffer();

            for (int y = srcStart.y(); y <= srcEnd.y(); y++) {
                for (int x = srcStart.x(); x <= srcEnd.x(); x++) {

                    int maskIndex =
                            object.voxels()
                                    .extent()
                                    .offset(x - srcStart.x(), y - srcStart.y());

                    if (maskBuffer.get(maskIndex) == maskOnVal) {

                        int srcIndex = getPlaneAccess().extent().offset(x, y);
                        srcArr.position(srcIndex);

                        if (isEqualTo(srcArr, equalVal)) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    public abstract int ceilOfMaxPixel();

    public abstract void copyItem(T srcBuffer, int srcIndex, T destBuffer, int destIndex);

    public abstract boolean isGreaterThan(T buffer, int operand);

    public abstract boolean isEqualTo(T buffer, int operand);

    public abstract boolean isEqualTo(T buffer1, T buffer2);

    public abstract void setAllPixelsTo(int val);

    public abstract void setPixelsTo(BoundingBox box, int val);

    public abstract void multiplyBy(double val);

    public abstract Voxels<T> maxIntensityProjection();

    public abstract Voxels<T> meanIntensityProjection();

    public void transferPixelsForPlane(int z, Voxels<T> src, int zSrc, boolean duplicate) {
        if (duplicate) {
            updateSlice(z, src.slice(zSrc));
        } else {
            updateSlice(z, src.slice(zSrc).duplicate());
        }
    }

    public void updateSlice(int z, VoxelBuffer<T> pixels) {
        planeAccess.setPixelsForPlane(z, pixels);
    }

    public VoxelBuffer<T> slice(int z) {
        return planeAccess.getPixelsForPlane(z);
    }

    public Extent extent() {
        return planeAccess.extent();
    }

    public int minSliceNonZero() {

        for (int z = 0; z < extent().z(); z++) {
            if (isSliceNonZero(z)) {
                return z;
            }
        }

        return -1;
    }

    public boolean isSliceNonZero(int z) {
        T pixels = getPlaneAccess().getPixelsForPlane(z).buffer();

        while (pixels.hasRemaining()) {
            if (!isEqualTo(pixels, 0)) {
                return true;
            }
        }
        return false;
    }

    public abstract void setVoxel(int x, int y, int z, int val);

    // Very slow access, use sparingly.  Instead process slice by slice.
    public abstract int getVoxel(int x, int y, int z);

    public int getVoxel(ReadableTuple3i point) {
        return getVoxel(point.x(), point.y(), point.z());
    }

    // Creates a new channel contain a duplication only of a particular slice
    public Voxels<T> extractSlice(int z) {

        Voxels<T> bufferAccess = factory.createInitialized(extent().duplicateChangeZ(1));
        bufferAccess.getPlaneAccess().setPixelsForPlane(0, getPlaneAccess().getPixelsForPlane(z));
        return bufferAccess;
    }

    /**
     * Creates a new voxels that are a resized version of the current voxels, interpolating as needed.
     *
     * <p>This is an IMMUTABLE operation.
     *
     * @param sizeX new size in X dimension
     * @param sizeY new size in Y dimension
     * @param interpolator means to interpolate pixels as they are resized.
     * @return newly created voxels of specified size containing interpolated pixels from the
     *     current voxels.
     */
    public Voxels<T> resizeXY(int sizeX, int sizeY, Interpolator interpolator) {

        Extent extentResized = new Extent(sizeX, sizeY, extent().z());

        Voxels<T> bufferTarget = factory.createInitialized(extentResized);

        assert (bufferTarget.slice(0).buffer().capacity()
                == extentResized.volumeXY());

        InterpolateUtilities.transferSlicesResizeXY(
                new VoxelsWrapper(this), new VoxelsWrapper(bufferTarget), interpolator);

        assert (bufferTarget.slice(0).buffer().capacity()
                == extentResized.volumeXY());
        return bufferTarget;
    }

    public Voxels<T> duplicate() {

        assert (getPlaneAccess().extent().z() > 0);

        Voxels<T> bufferAccess = factory.createInitialized(getPlaneAccess().extent());

        for (int z = 0; z < extent().z(); z++) {
            VoxelBuffer<T> buffer = slice(z);
            bufferAccess.updateSlice(z, buffer.duplicate());
        }

        return bufferAccess;
    }

    /**
     * Is the buffer identical to another beautiful (deep equals)
     *
     * @param other
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean equalsDeep(Voxels<?> other) {

        if (!factory.dataType().equals(other.getFactory().dataType())) {
            return false;
        }

        if (!extent().equals(other.extent())) {
            return false;
        }

        for (int z = 0; z < getPlaneAccess().extent().z(); z++) {

            VoxelBuffer<T> buffer1 = getPlaneAccess().getPixelsForPlane(z);
            VoxelBuffer<T> buffer2 = (VoxelBuffer<T>) other.getPlaneAccess().getPixelsForPlane(z);

            while (buffer1.buffer().hasRemaining()) {

                if (!isEqualTo(buffer1.buffer(), buffer2.buffer())) {
                    return false;
                }
            }

            assert (!buffer2.buffer().hasRemaining());
        }

        return true;
    }

    public abstract void max(Voxels<T> other) throws OperationFailedException;

    public VoxelsFactoryTypeBound<T> getFactory() {
        return factory;
    }

    private Voxels<T> regionAvoidNewIfPossible(BoundingBox box) {

        if (box.equals(new BoundingBox(extent()))
                && box.cornerMin().x() == 0
                && box.cornerMin().y() == 0
                && box.cornerMin().z() == 0) {
            return this;
        }
        return regionAlwaysNew(box);
    }

    private Voxels<T> regionAlwaysNew(BoundingBox box) {

        // Otherwise we create a new buffer
        Voxels<T> voxelsOut = factory.createInitialized(box.extent());
        copyPixelsTo(box, voxelsOut, new BoundingBox(box.extent()));
        return voxelsOut;
    }

    private static void checkExtentMatch(BoundingBox box1, BoundingBox box2) {
        Extent extent1 = box1.extent();
        Extent extent2 = box2.extent();
        if (!extent1.equals(extent2)) {
            throw new IllegalArgumentException(
                    String.format(
                            "The extents of the two bounding-boxes are not identical: %s vs %s",
                            extent1, extent2));
        }
    }
}
