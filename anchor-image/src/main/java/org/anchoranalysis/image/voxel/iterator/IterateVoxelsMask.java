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
package org.anchoranalysis.image.voxel.iterator;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.arithmetic.RunningSum;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferUnaryWithPoint;

/**
 * Utilities for iterating over the subset of voxels corresponding to an <i>on</i> state in a {@link
 * Mask}.
 *
 * <p>The utilities operate on one or more {@link Voxels}. A processor is called on each selected
 * voxel.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxelsMask {

    /**
     * Iterate over each voxel in a mask - with an associated buffer for each slice from a voxel-bo
     *
     * @param mask the mask is used as a condition on what voxels to iterate i.e. only voxels within
     *     these bounds
     * @param voxels voxels to iterate over (if the corresponding mask voxel is <i>on</i>)
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T> void withBuffer(
            Mask mask, Voxels<T> voxels, ProcessBufferUnaryWithPoint<T> process) {
        // Treat it as one giant object box. This will involve some additions and subtractions of 0
        // during the processing of voxels
        // but after some quick emperical checks, it doesn't seem to make a performance difference.
        // Probably the JVM is smart enough
        // to optimize away these redundant calcualations.
        IterateVoxelsObjectMask.withBuffer(new ObjectMask(mask.binaryVoxels()), voxels, process);
    }

    /**
     * Calculates the sum and count across voxels intensity that correspond to <i>on</i> voxels on a
     * <i>mask</i>
     *
     * <p>The {@code mask} must have equal extent to {@code voxelsIntensity}
     *
     * @param voxelsIntensity the voxels whose intensity we wish to find the mean of (subject to
     *     {@code mask}
     * @param mask only voxels who correspond to an ON voxels in the mask are included
     * @return the running-sum
     */
    public static RunningSum calculateRunningSum(
            Mask mask, Voxels<UnsignedByteBuffer> voxelsIntensity) {
        Preconditions.checkArgument(voxelsIntensity.extent().equals(mask.extent()));

        RunningSum running = new RunningSum();

        IterateVoxelsMask.withBuffer(
                mask,
                voxelsIntensity,
                (point, buffer, offset) -> addFromBufferToRunning(buffer, offset, running));

        return running;
    }

    private static void addFromBufferToRunning(
            UnsignedByteBuffer buffer, int offset, RunningSum running) {
        running.increment(buffer.getUnsigned(offset), 1);
    }
}
