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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.arithmetic.Counter;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsBoundingBox;
import org.anchoranalysis.image.voxel.iterator.predicate.PredicateTwoBytes;

/**
 * Like {@link IterateVoxelsBoundingBox} but counts voxels matching a predicate rather than
 * iterating.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountVoxelsIntersectingBounded {

    /**
     * Counts all voxels in the intersection of two bounded-voxels of type {@link BoundedVoxels}
     * that match a predicate.
     *
     * @param voxels1 the first bounded-voxels
     * @param voxels2 the second bounded-voxels
     * @param predicate determines if a particular voxel should be counted or not?
     * @return the number of voxels that match the predicate.
     */
    public static int countByte(
            BoundedVoxels<UnsignedByteBuffer> voxels1,
            BoundedVoxels<UnsignedByteBuffer> voxels2,
            PredicateTwoBytes predicate) {
        Counter counter = new Counter();
        IterateVoxelsIntersectingBounded.withTwoBuffers(
                voxels1,
                voxels2,
                (point, buffer1, buffer2, offset1, offset2) -> {
                    if (predicate.test(buffer1.getRaw(offset1), buffer2.getRaw(offset2))) {
                        counter.increment();
                    }
                });
        return counter.getCount();
    }

    /**
     * Counts all voxels intersection of two bounded-voxels of type {@link BoundedVoxels} but only
     * voxels that lie on an object-mask and match a predicate.
     *
     * @param maskGlobal a mask defined on the entire global space, and all matching voxels must
     *     have an <i>on</i> value in this mask, in addition to being part of the intersection of
     *     {@code voxels1} and {@code voxels2}.
     * @param onMaskGlobal the <i>on</i> value in {@code maskGlobal}.
     * @param voxels1 the first bounded-voxels
     * @param voxels2 the second bounded-voxels
     * @param predicate determines if a particular voxel should be counted or not?
     * @return the number of voxels that match the predicate.
     */
    public static int countByteMasked(
            Voxels<UnsignedByteBuffer> maskGlobal,
            byte onMaskGlobal,
            BoundedVoxels<UnsignedByteBuffer> voxels1,
            BoundedVoxels<UnsignedByteBuffer> voxels2,
            PredicateTwoBytes predicate) {
        Counter counter = new Counter();
        IterateVoxelsIntersectingBounded.withTwoBuffers(
                maskGlobal,
                onMaskGlobal,
                voxels1,
                voxels2,
                (point, buffer1, buffer2, offset1, offset2) -> {
                    if (predicate.test(buffer1.getRaw(offset1), buffer2.getRaw(offset2))) {
                        counter.increment();
                    }
                });
        return counter.getCount();
    }
}
