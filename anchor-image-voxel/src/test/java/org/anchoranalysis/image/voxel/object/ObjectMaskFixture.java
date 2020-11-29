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

package org.anchoranalysis.image.voxel.object;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point2i;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Creates object-masks of a certain shape.
 *
 * <p>The object-masks are entirely filled-in (rectangular to fill bounding-box) or filled-in except
 * single-voxel corners in the X and Y dimensions.
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
    /** Whether to remove single-voxel pixels from corners or not */
    private final boolean removeCorners;

    /** Whether to create a 3D object or a 2D object. */
    private final boolean do3D;
    // END REQUIRED ARGUMENTS

    public ObjectMask filledMask(Point2i corner) {
        return filledMask(corner.x(), corner.y());
    }

    public ObjectMask filledMask(int cornerX, int cornerY) {
        return filledMask(cornerX, cornerY, WIDTH, HEIGHT);
    }

    /** A rectangular object-mask with single-pixel corners removed */
    public ObjectMask filledMask(int cornerX, int cornerY, int width, int height) {
        Point3i corner = new Point3i(cornerX, cornerY, 0);
        Extent extent = new Extent(width, height, do3D ? DEPTH : 1);

        ObjectMask object = new ObjectMask(new BoundingBox(corner, extent));
        object.assignOn().toAll();
        if (removeCorners) {
            removeEachCorner(object);
        }
        return object;
    }

    private void removeEachCorner(ObjectMask object) {

        BinaryVoxels<UnsignedByteBuffer> binaryValues = object.binaryVoxels();

        Extent e = object.boundingBox().extent();
        int widthMinusOne = e.x() - 1;
        int heightMinusOne = e.y() - 1;

        for (int z = 0; z < e.z(); z++) {
            binaryValues.setOff(0, 0, z);
            binaryValues.setOff(widthMinusOne, 0, z);
            binaryValues.setOff(0, heightMinusOne, z);
            binaryValues.setOff(widthMinusOne, heightMinusOne, z);
        }
    }
}
