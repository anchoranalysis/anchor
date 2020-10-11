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
package org.anchoranalysis.image.voxel.iterator.intersecting;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsBoundingBox;
import org.anchoranalysis.image.voxel.iterator.predicate.buffer.PredicateBufferBinary;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferBinary;

/**
 * Like {@link IterateVoxelsBoundingBox} but specifically for processing areas of intersection
 * between {@link BoundedVoxels} of type {@link UnsignedByteBuffer}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxelsIntersectingBounded {

    /**
     * Iterates over the intersection of two bounded-voxels of type {@link BoundedVoxels}.
     *
     * @param voxels1 the first bounded-voxels
     * @param voxels2 the second bounded-voxels
     * @param <T> voxel buffer data type
     * @param process called for every voxel in the intersection containing the respective buffers
     *     and offsets for {@code first} and {@code second}.
     */
    public static <T> void withTwoBuffers(
            BoundedVoxels<T> voxels1, BoundedVoxels<T> voxels2, ProcessBufferBinary<T> process) {
        // Find the common bounding box
        Optional<Intersection> intersection = findIntersection(voxels1, voxels2);

        if (intersection.isPresent()) {
            IterateVoxelsBoundingBox.withTwoBuffers(
                    intersection.get().intersectingBox(),
                    intersection.get().relative(),
                    voxels1.voxels(),
                    voxels2.voxels(),
                    process);
        }
    }

    /**
     * Iterates over the intersection of two bounded-voxels of type {@link BoundedVoxels} but only
     * voxels that lie on an object-mask.
     *
     * @param maskGlobal a mask defined on the entire global space, and all matching voxels must
     *     have an <i>on</i> value in this mask, in addition to being part of the intersection of
     *     {@code voxels1} and {@code voxels2}.
     * @param onMaskGlobal the <i>on</i> value in {@code maskGlobal}.
     * @param voxels1 the first bounded-voxels
     * @param voxels2 the second bounded-voxels
     * @param process called for every matching voxel in the intersection containing the respective
     *     buffers and offsets for {@code first} and {@code second}.
     */
    public static void withTwoBuffers(
            Voxels<UnsignedByteBuffer> maskGlobal,
            byte onMaskGlobal,
            BoundedVoxels<UnsignedByteBuffer> voxels1,
            BoundedVoxels<UnsignedByteBuffer> voxels2,
            ProcessBufferBinary<UnsignedByteBuffer> process) {
        // Find the common bounding box
        Optional<Intersection> intersection = findIntersection(voxels1, voxels2);

        if (intersection.isPresent()) {
            IterateVoxelsBoundingBox.withThreeBuffers(
                    intersection.get().intersectingBox(),
                    intersection.get().relative(),
                    voxels1.boundingBox().cornerMin(),
                    voxels1.voxels(),
                    voxels2.voxels(),
                    maskGlobal,
                    new CheckMask(process, onMaskGlobal));
        }
    }

    /**
     * Iterates over the intersection of two bounded-voxels of type {@link BoundedVoxels} until a
     * predicate is satisfied.
     *
     * @param voxels1 the first bounded-voxels
     * @param voxels2 the second bounded-voxels
     * @param <T> voxel buffer data type
     * @return Point3i if intersection exists, then the first point of intersection found
     *     (newly-created), or else empty if no intersection exists
     */
    public static <T> Optional<Point3i> withTwoBuffersUntil(
            BoundedVoxels<T> voxels1,
            BoundedVoxels<T> voxels2,
            PredicateBufferBinary<T> predicate) {

        // Find the common bounding box
        Optional<Intersection> intersection = findIntersection(voxels1, voxels2);

        if (intersection.isPresent()) {
            return IterateVoxelsBoundingBox.withTwoBuffersUntil(
                    intersection.get().intersectingBox(),
                    intersection.get().relative(),
                    voxels1.voxels(),
                    voxels2.voxels(),
                    predicate);
        } else {
            return Optional.empty();
        }
    }

    /** Calculates the intersection of two {@link BoundedVoxels} if it exists. */
    private static <T> Optional<Intersection> findIntersection(
            BoundedVoxels<T> voxels1, BoundedVoxels<T> voxels2) {
        Optional<BoundingBox> boxIntersection =
                voxels1.boundingBox().intersection().with(voxels2.boundingBox());
        return boxIntersection.map(
                box -> Intersection.create(voxels1.boundingBox(), voxels2.boundingBox(), box));
    }
}
