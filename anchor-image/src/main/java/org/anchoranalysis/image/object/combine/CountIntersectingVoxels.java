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

package org.anchoranalysis.image.object.combine;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.voxel.BoundedVoxels;

public abstract class CountIntersectingVoxels {

    /**
     * Calculates the number of intersecting pixels between two binary-voxels of identical size
     *
     * @param src
     * @param other
     * @return
     */
    public int countIntersectingVoxels(
            BoundedVoxels<ByteBuffer> src, BoundedVoxels<ByteBuffer> other) {
        // Find the common bounding box
        Optional<BoundingBox> boxIntersect =
                src.boundingBox().intersection().with(other.boundingBox());

        if (!boxIntersect.isPresent()) {
            // If the bounding boxes don't intersect then we can
            //   go home early
            return 0;
        }

        return countIntersectingVoxelsFromBBox(src, other, boxIntersect.get());
    }

    // count intersecting pixels
    private int countIntersectingVoxelsFromBBox(
            BoundedVoxels<ByteBuffer> src,
            BoundedVoxels<ByteBuffer> other,
            BoundingBox boxIntersect) {
        IntersectionBoundingBox box =
                IntersectionBoundingBox.create(
                        src.boundingBox(), other.boundingBox(), boxIntersect);

        // Otherwise we count the number of pixels that are not empty
        //  in both bounded-voxels in the intersecting region
        int cnt = 0;
        for (int z = box.z().min(); z < box.z().max(); z++) {

            ByteBuffer buffer = src.voxels().sliceBuffer(z);

            int zOther = z + box.z().rel();
            ByteBuffer bufferOther = other.voxels().sliceBuffer(zOther);

            cnt += countIntersectingVoxels(buffer, bufferOther, box);
        }

        return cnt;
    }

    protected abstract int countIntersectingVoxels(
            ByteBuffer buffer1, ByteBuffer buffer2, IntersectionBoundingBox box);
}
