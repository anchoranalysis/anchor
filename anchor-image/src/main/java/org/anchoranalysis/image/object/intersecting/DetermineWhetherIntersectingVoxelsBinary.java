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

package org.anchoranalysis.image.object.intersecting;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.voxel.BoundedVoxels;

/**
 * Determines whether there are any intersecting voxels between binary-masks, exiting as soon as one
 * is encountered (as early as possible)
 *
 * @author Owen Feehan
 */
public class DetermineWhetherIntersectingVoxelsBinary {

    private byte byteOn1;
    private byte byteOn2;

    public DetermineWhetherIntersectingVoxelsBinary(BinaryValuesByte bvb1, BinaryValuesByte bvb2) {
        super();
        this.byteOn1 = bvb1.getOnByte();
        this.byteOn2 = bvb2.getOnByte();
    }

    public boolean hasIntersectingVoxels(
            BoundedVoxels<ByteBuffer> src, BoundedVoxels<ByteBuffer> other) {
        return pointOfFirstIntersectingVoxel(src, other).isPresent();
    }

    private Optional<Point3i> pointOfFirstIntersectingVoxel(
            BoundedVoxels<ByteBuffer> src, BoundedVoxels<ByteBuffer> other) {

        // Find the common bounding box
        Optional<BoundingBox> boxIntersect =
                src.boundingBox().intersection().with(other.boundingBox());
        return boxIntersect.flatMap(box -> hasIntersectingVoxelsInBoundingBox(src, other, box));
    }

    /**
     * @param src
     * @param other
     * @param boxIntersect
     * @param onMask1
     * @param onMask2
     * @return Point3i if intersection exists, then the first point of intersection found
     *     (newly-created), or else empty if no intersection exists
     */
    private Optional<Point3i> hasIntersectingVoxelsInBoundingBox(
            BoundedVoxels<ByteBuffer> src,
            BoundedVoxels<ByteBuffer> other,
            BoundingBox boxIntersect) {

        IntersectionBBox box =
                IntersectionBBox.create(
                        src.boundingBox(), other.boundingBox(), boxIntersect);

        // Otherwise we count the number of pixels that are not empty
        //  in both bounded-voxels in the intersecting region
        for (int z = box.z().min(); z < box.z().max(); z++) {

            ByteBuffer buffer = src.voxels().sliceBuffer(z);

            int zOther = z + box.z().rel();
            ByteBuffer bufferOther = other.voxels().sliceBuffer(zOther);

            buffer.clear();
            bufferOther.clear();

            Optional<Point3i> intersectingPoint = hasIntersectingVoxels(buffer, bufferOther, box);
            if (intersectingPoint.isPresent()) {
                intersectingPoint.get().setZ(z);
                return intersectingPoint;
            }
        }

        return Optional.empty();
    }

    /**
     * @return Point3i NULL if no intersection exists, otherwise first point of intersection found
     *     (newly-created)
     */
    private Optional<Point3i> hasIntersectingVoxels(
            ByteBuffer buffer1, ByteBuffer buffer2, IntersectionBBox box) {

        for (int y = box.y().min(); y < box.y().max(); y++) {
            int yOther = y + box.y().rel();

            for (int x = box.x().min(); x < box.x().max(); x++) {
                int xOther = x + box.x().rel();

                byte posCheck = buffer1.get(box.e1().offset(x, y));
                byte posCheckOther = buffer2.get(box.e2().offset(xOther, yOther));

                if (posCheck == byteOn1 && posCheckOther == byteOn2) {
                    return Optional.of(new Point3i(x, y, 0));
                }
            }
        }
        return Optional.empty();
    }
}
