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
package org.anchoranalysis.image.voxel.arithmetic;

import java.util.function.IntFunction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Base class for implementing voxel-arithmetic with a buffer of a particular type
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class Base<T> implements VoxelsArithmetic {

    // START REQUIRED ARGUMENTS
    /** The extent of the voxels on which arithmetic is to be performed */
    private final Extent extent;

    /** A buffer for a particular slice index (set at the initial position in the buffer) */
    private final IntFunction<T> bufferForSlice;
    // END REQUIRED ARGUMENTS

    @Override
    public void multiplyBy(double factor) {
        if (factor != 1) {
            extent.iterateOverZ(z -> multiplyBuffer(bufferForSlice.apply(z), factor));
        }
    }

    @Override
    public void divideBy(int divisor) {
        if (divisor != 1) {
            extent.iterateOverZ(z -> divideByBuffer(bufferForSlice.apply(z), divisor));
        }
    }

    @Override
    public void subtractFrom(int valueToSubtractFrom) {

        for (int z = 0; z < extent.z(); z++) {
            subtractFromBuffer(bufferForSlice.apply(z), valueToSubtractFrom);
        }
    }

    @Override
    public void addTo(ObjectMask object, int valueToBeAdded) {

        BoundingBox box = object.boundingBox();

        byte maskOnByte = object.binaryValuesByte().getOnByte();

        ReadableTuple3i pointMax = box.calculateCornerMaxExclusive();
        for (int z = box.cornerMin().z(); z < pointMax.z(); z++) {

            T pixels = bufferForSlice.apply(z);
            UnsignedByteBuffer pixelsMask = object.sliceBufferGlobal(z);

            for (int y = box.cornerMin().y(); y < pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x < pointMax.x(); x++) {

                    if (pixelsMask.getRaw() == maskOnByte) {
                        addToBufferIndex(pixels, extent.offset(x, y), valueToBeAdded);
                    }
                }
            }
        }
    }

    @Override
    public void multiplyBy(ObjectMask object, double factor) {

        BoundingBox box = object.boundingBox();

        byte maskOnByte = object.binaryValuesByte().getOnByte();

        ReadableTuple3i pointMax = box.calculateCornerMax();
        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            T pixels = bufferForSlice.apply(z);
            UnsignedByteBuffer pixelsMask = object.sliceBufferGlobal(z);

            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    if (pixelsMask.getRaw() == maskOnByte) {
                        int index = extent.offset(x, y);

                        multiplyByBufferIndex(pixels, index, factor);
                    }
                }
            }
        }
    }

    /**
     * Subtracts the voxel at the current position in a buffer from a constant i.e. by calling
     * {@code get()}
     *
     * <p>Note the buffer's position will be advanced by one after this call.
     *
     * @param buffer the buffer, which must have its position set to the first item
     * @param valueToSubtractFrom what to subtract from
     */
    protected abstract void subtractFromBuffer(T buffer, int valueToSubtractFrom);

    /**
     * Multiplies the voxel at the current position in a buffer i.e. by calling {@code get()}
     *
     * <p>Note the buffer's position will be advanced by one after this call.
     *
     * @param buffer the buffer, which must have its position set to the first item
     * @param factor what to multiply the voxel by
     */
    protected abstract void multiplyBuffer(T buffer, double factor);

    /**
     * Divides the voxel at the current position in a buffer (i.e. by calling {@code get()}) by a
     * scalar constant.
     *
     * <p>Note the buffer's position will be advanced by one after this call.
     *
     * @param buffer the buffer, which must have its position set to the first item.
     * @param divisor the scalar constant to divide the voxel by.
     */
    protected abstract void divideByBuffer(T buffer, int divisor);

    /**
     * Multiplies the voxel at a particular position in a buffer
     *
     * @param buffer the buffer
     * @param index the index in the position of the voxel to change
     * @param factor what to multiply the voxel by
     */
    protected abstract void multiplyByBufferIndex(T buffer, int index, double factor);

    /**
     * Adds a constant to the voxel at a particular position in a buffer
     *
     * @param buffer the buffer
     * @param index the index in the position of the voxel to change
     * @param valueToBeAdded constant to be added
     */
    protected abstract void addToBufferIndex(T buffer, int index, int valueToBeAdded);
}
