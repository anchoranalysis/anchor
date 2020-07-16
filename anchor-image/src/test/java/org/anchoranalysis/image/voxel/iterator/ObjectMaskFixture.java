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
package org.anchoranalysis.image.voxel.iterator;

import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Creates object-masks of a certain shape
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class ObjectMaskFixture {

    public static final int WIDTH = 40;
    public static final int HEIGHT = 50;
    public static final int DEPTH = 15;

    public static final int VOXELS_REMOVED_CORNERS = 4;
    public static final int BOUNDING_BOX_NUM_VOXELS = WIDTH * HEIGHT;
    public static final int OBJECT_NUM_VOXELS_2D = BOUNDING_BOX_NUM_VOXELS - VOXELS_REMOVED_CORNERS;
    public static final int OBJECT_NUM_VOXELS_3D = OBJECT_NUM_VOXELS_2D * ObjectMaskFixture.DEPTH;

    // START REQUIRED ARGUMENTS
    private final boolean do3D;
    // END REQUIRED ARGUMENTS

    public ObjectMask filledMask(int cornerX, int cornerY) {
        return filledMask(cornerX, cornerY, WIDTH, HEIGHT);
    }

    /** A rectangular mask with single-pixel corners removed */
    public ObjectMask filledMask(int cornerX, int cornerY, int width, int height) {
        Point3i corner = new Point3i(cornerX, cornerY, 0);
        Extent extent = new Extent(width, height, do3D ? DEPTH : 1);

        ObjectMask object = new ObjectMask(new BoundingBox(corner, extent));
        object.binaryVoxelBox().setAllPixelsToOn();
        removeEachCorner(object);
        return object;
    }

    private void removeEachCorner(ObjectMask object) {

        BinaryVoxelBox<ByteBuffer> bvb = object.binaryVoxelBox();

        Extent e = object.getBoundingBox().extent();
        int widthMinusOne = e.getX() - 1;
        int heightMinusOne = e.getY() - 1;

        for (int z = 0; z < e.getZ(); z++) {
            bvb.setOff(0, 0, z);
            bvb.setOff(widthMinusOne, 0, z);
            bvb.setOff(0, heightMinusOne, z);
            bvb.setOff(widthMinusOne, heightMinusOne, z);
        }
    }
}
