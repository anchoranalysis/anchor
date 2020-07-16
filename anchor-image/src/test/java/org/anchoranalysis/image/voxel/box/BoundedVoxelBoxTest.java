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
/* (C)2020 */
package org.anchoranalysis.image.voxel.box;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.junit.Test;

public class BoundedVoxelBoxTest {

    /**
     * Grows an object which is already partially outside the clip region
     *
     * @throws OperationFailedException
     */
    @Test(expected = OperationFailedException.class)
    public void testGrowObjectOutsideClipRegion() throws OperationFailedException {

        // A bounding box that overlaps with the extent
        Extent extent = new Extent(20, 20, 20);

        BoundedVoxelBox<ByteBuffer> box =
                new BoundedVoxelBox<ByteBuffer>(
                        new BoundingBox(new Point3i(10, 10, 10), new Extent(15, 15, 15)),
                        VoxelBoxFactory.getByte());

        Point3i grow = new Point3i(1, 1, 1);
        box.growBuffer(grow, grow, Optional.of(extent), VoxelBoxFactory.getByte());
    }
}
