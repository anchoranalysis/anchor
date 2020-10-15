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
package org.anchoranalysis.image.voxel.extracter.predicate;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.arithmetic.Counter;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsObjectMask;

/**
 * Implementation of {@link VoxelsPredicate} for a particular {@link Voxels}.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@RequiredArgsConstructor
public class PredicateImplementation<T> implements VoxelsPredicate {

    // START REQUIRED ARGUMENTS
    /** The voxels on which the predicate is based. */
    private final Voxels<T> voxels;
    // END REQUIRED ARGUMENTS

    /** Are there voxels remaining in the buffer? */
    public static interface SetBufferPosition<T> {
        void position(T buffer, int offset);
    }

    // START REQUIRED ARGUMENTS
    /** Checks if the current value of a buffer matches a predicate */
    private final Predicate<T> predicate;
    // END REQUIRED ARGUMENTS

    @Override
    public boolean anyExists() {

        int zMax = voxels.extent().z();

        for (int z = 0; z < zMax; z++) {

            VoxelBuffer<T> buffer = voxels.slice(z);
            while (buffer.hasRemaining()) {

                if (predicate.test(buffer.buffer())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int count() {

        int count = 0;
        int zMax = voxels.extent().z();

        for (int z = 0; z < zMax; z++) {
            VoxelBuffer<T> buffer = voxels.slice(z);

            while (buffer.hasRemaining()) {
                if (predicate.test(buffer.buffer())) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public int countForObject(ObjectMask object) {

        Counter counter = new Counter();

        IterateVoxelsObjectMask.withVoxelBuffer(object, voxels, (buffer, offset) -> {
            buffer.position(offset);
            if (predicate.test(buffer.buffer())) {
                counter.increment();
            }            
        });
        
        return counter.getCount();
    }

    @Override
    public boolean higherCountExistsThan(int threshold) {
        int count = 0;
        int zMax = voxels.extent().z();

        for (int z = 0; z < zMax; z++) {
            VoxelBuffer<T> buffer = voxels.slice(z);

            while (buffer.hasRemaining()) {
                if (predicate.test(buffer.buffer())) {
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
        int zMax = voxels.extent().z();

        for (int z = 0; z < zMax; z++) {
            VoxelBuffer<T> buffer = voxels.slice(z);

            while (buffer.hasRemaining()) {
                if (predicate.test(buffer.buffer())) {
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

            VoxelBuffer<T> pixelIn = voxels.slice(z);
            UnsignedByteBuffer pixelOut = object.sliceBufferGlobal(z);

            int indexMask = 0;
            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    int index = voxels.extent().offset(x, y);
                    pixelIn.position(index);

                    if (predicate.test(pixelIn.buffer())) {
                        pixelOut.putRaw(indexMask, outOn);
                    }

                    indexMask++;
                }
            }
        }

        return object;
    }
}
