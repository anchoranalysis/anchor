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

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.anchoranalysis.image.voxel.iterator.process.ProcessPoint;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferUnaryWithPoint;

/**
 * Utilities for iterating over the subset of voxels corresponding to an <i>on</i> state in an
 * optional {@link ObjectMask}.
 *
 * <p>If the {@link ObjectMask} is not defined, then <i>all</i> voxels are iterated over.
 *
 * <p>The utilities operate on one or more {@link Voxels}. A processor is called on each selected
 * voxel.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxelsObjectMaskOptional {

    /**
     * Iterate over all points that are located on a object-mask or else all points in an extent.
     *
     * @param objectMask an optional object-mask that is used as a condition on what voxels to
     *     iterate. If not defined, all voxels are iterated over.
     * @param extent if object-mask isn't defined, then all the voxels in this {@link Extent} are
     *     iterated over instead
     * @param process process is called for each voxel (on the entire {@link Extent} or on the
     *     object-mask depending) using <i>global</i> coordinates.
     */
    public static void withPoint(
            Optional<ObjectMask> objectMask, Extent extent, ProcessPoint process) {
        if (objectMask.isPresent()) {
            IterateVoxelsObjectMask.withPoint(objectMask.get(), process);
        } else {
            IterateVoxelsAll.withPoint(extent, process);
        }
    }

    /**
     * Iterate over each voxel on an object-mask with <b>one</b> associated <b>buffer</b>.
     *
     * @param objectMask an optional object-mask that is used as a condition on what voxels to
     *     iterate. If not defined, all voxels are iterated over.
     * @param voxels voxels where buffers extracted from be processed, and which define the global
     *     coordinate space
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T> void withBuffer(
            Optional<ObjectMask> objectMask, Voxels<T> voxels, ProcessBufferUnaryWithPoint<T> process) {

        // Note the offsets must be added before any additional restriction like an object-mask, to
        // make
        // sure they are calculated for EVERY process.
        // Therefore we {@link AddOffsets} must be interested as the top-most level in the
        // processing chain
        // (i.e. {@link AddOffsets} must delegate to {@link RequireIntersectionWithMask} but not the
        // other way round.
        if (objectMask.isPresent()) {
            IterateVoxelsObjectMask.withBuffer(objectMask.get(), voxels, process);
        } else {
            IterateVoxelsAll.withBuffer(voxels, process);
        }
    }

    /**
     * Iterate over each voxel in a sliding-buffer, optionally restricting it to be only voxels in a
     * certain object
     *
     * @param buffer a sliding-buffer whose voxels are iterated over, partially (if an objectmask is
     *     defined) or as a whole (if no onject-mask is defined)
     * @param objectMask an optional object-mask that is used as a condition on what voxels to
     *     iterate
     * @param process process is called for each voxel (on the entire {@link SlidingBuffer} or on
     *     the object-mask depending) using <i>global</i> coordinates.
     */
    public static void withSlidingBuffer(
            Optional<ObjectMask> objectMask, SlidingBuffer<?> buffer, ProcessPoint process) {

        buffer.seek(objectMask.map(object -> object.boundingBox().cornerMin().z()).orElse(0));

        withPoint(objectMask, buffer.extent(), new SlidingBufferProcessor(buffer, process));
    }
}
