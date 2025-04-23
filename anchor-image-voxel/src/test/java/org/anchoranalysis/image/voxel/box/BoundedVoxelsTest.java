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

package org.anchoranalysis.image.voxel.box;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.junit.jupiter.api.Test;

class BoundedVoxelsTest {

    /**
     * Grows an object which is already partially outside the clip region
     *
     * @throws OperationFailedException
     */
    @Test
    void testGrowObjectOutsideClampRegion() {
        assertThrows(
                OperationFailedException.class,
                () -> {
                    // A bounding box that overlaps with the extent
                    Extent extent = extent(20);

                    BoundedVoxels<UnsignedByteBuffer> box =
                            VoxelsFactory.getUnsignedByte()
                                    .createBounded(BoundingBox.createReuse(point(10), extent(15)));

                    Point3i grow = point(1);
                    box.growBuffer(
                            grow, grow, Optional.of(extent), VoxelsFactory.getUnsignedByte());
                });
    }

    private static Point3i point(int value) {
        return new Point3i(value, value, value);
    }

    private static Extent extent(int value) {
        return new Extent(value, value, value);
    }
}
