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
package org.anchoranalysis.image.voxel.extracter;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.VoxelsPredicate;

/**
 * Implementation of {@link VoxelsPredicate} for a particular buffer provider
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@RequiredArgsConstructor
class PredicateImplementation<T extends Buffer> implements VoxelsPredicate {

    // START REQUIRED ARGUMENTS
    /** The extent of the voxels on which the predicate is to be performed */
    private final Extent extent;

    /** A buffer for a particular slice index */
    private final IntFunction<T> bufferForSlice;

    /** Checks if the current value of a buffer matches a predicate */
    private final Predicate<T> predicate;
    // END REQUIRED ARGUMENTS

    @Override
    public boolean anyExists() {

        int zMax = extent.z();

        for (int z = 0; z < zMax; z++) {

            T buffer = bufferForSlice.apply(z);
            while (buffer.hasRemaining()) {

                if (predicate.test(buffer)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int count() {

        int count = 0;
        int zMax = extent.z();

        for (int z = 0; z < zMax; z++) {
            T buffer = bufferForSlice.apply(z);

            while (buffer.hasRemaining()) {
                if (predicate.test(buffer)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public int countForObject(ObjectMask object) {

        ReadableTuple3i srcStart = object.boundingBox().cornerMin();
        ReadableTuple3i srcEnd = object.boundingBox().calculateCornerMax();

        int count = 0;

        byte maskOnVal = object.binaryValuesByte().getOnByte();

        for (int z = srcStart.z(); z <= srcEnd.z(); z++) {

            T srcArr = bufferForSlice.apply(z);
            ByteBuffer maskBuffer = object.sliceBufferGlobal(z);

            for (int y = srcStart.y(); y <= srcEnd.y(); y++) {
                for (int x = srcStart.x(); x <= srcEnd.x(); x++) {

                    if (maskBuffer.get(object.offsetGlobal(x, y)) == maskOnVal) {

                        int srcIndex = extent.offset(x, y);
                        srcArr.position(srcIndex);

                        if (predicate.test(srcArr)) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    @Override
    public boolean higherCountExistsThan(int threshold) {
        int count = 0;
        int zMax = extent.z();

        for (int z = 0; z < zMax; z++) {
            T buffer = bufferForSlice.apply(z);

            while (buffer.hasRemaining()) {
                if (predicate.test(buffer)) {
                    if (count == threshold) {
                        // We've reached the threshold, positive outcome
                        return true;
                    } else {
                        count++;
                    }
                }
            }
        }
        // We've never reached the threshold, negative outcome
        return false;
    }

    @Override
    public boolean lowerCountExistsThan(int threshold) {
        int count = 0;
        int zMax = extent.z();

        for (int z = 0; z < zMax; z++) {
            T buffer = bufferForSlice.apply(z);

            while (buffer.hasRemaining()) {
                if (predicate.test(buffer)) {
                    count++;
                    if (count == threshold) {
                        // We've reached the threshold, negative outcome
                        return false;
                    }
                }
            }
        }
        // We've never reached the threshold, positive outcome
        return true;
    }

    @Override
    public ObjectMask deriveObject(BoundingBox box) {

        ObjectMask object = new ObjectMask(box);

        ReadableTuple3i pointMax = box.calculateCornerMax();

        byte outOn = object.binaryValuesByte().getOnByte();

        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            T pixelIn = bufferForSlice.apply(z);
            ByteBuffer pixelOut = object.sliceBufferGlobal(z);

            int ind = 0;
            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    int index = extent.offset(x, y);
                    pixelIn.position(index);

                    if (predicate.test(pixelIn)) {
                        pixelOut.put(ind, outOn);
                    }

                    ind++;
                }
            }
        }

        return object;
    }
}
