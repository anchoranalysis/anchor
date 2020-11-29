/*-
 * #%L
 * anchor-test-image
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

package org.anchoranalysis.test.image.object;

import static org.junit.Assert.assertTrue;

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;

@AllArgsConstructor
public class ObjectMaskFixture {

    private final Dimensions dimensions;

    public ObjectMask create1() {
        Extent extent = new Extent(20, 34, 11);
        CutOffCorners pattern = new CutOffCorners(3, 2, extent);
        return createAt(new Point3i(10, 15, 3), extent, pattern);
    }

    public ObjectMask create2() {
        Extent extent = new Extent(19, 14, 5);
        CutOffCorners pattern = new CutOffCorners(5, 1, extent);
        return createAt(new Point3i(3, 1, 7), extent, pattern);
    }

    public ObjectMask create3() {
        Extent extent = new Extent(19, 14, 13);
        CutOffCorners pattern = new CutOffCorners(1, 5, extent);
        return createAt(new Point3i(17, 15, 2), extent, pattern);
    }

    private ObjectMask createAt(Point3i cornerMin, Extent extent, VoxelPattern pattern) {
        BoundingBox box = new BoundingBox(cornerMin, extent);

        assertTrue(dimensions.contains(box));

        Voxels<UnsignedByteBuffer> voxels =
                VoxelsFactory.getUnsignedByte().createInitialized(extent);
        BinaryValues binaryValues = BinaryValues.getDefault();
        BinaryValuesByte binaryValuesByte = binaryValues.createByte();

        boolean atLeastOneHigh = false;

        for (int z = 0; z < extent.z(); z++) {
            VoxelBuffer<UnsignedByteBuffer> slice = voxels.slice(z);

            for (int y = 0; y < extent.y(); y++) {
                for (int x = 0; x < extent.x(); x++) {
                    byte toPut;
                    if (pattern.isPixelOn(x, y, z)) {
                        toPut = binaryValuesByte.getOnByte();
                        atLeastOneHigh = true;
                    } else {
                        toPut = binaryValuesByte.getOffByte();
                    }
                    slice.putByte(extent.offset(x, y), toPut);
                }
            }
        }

        assertTrue(atLeastOneHigh);

        return new ObjectMask(box, BinaryVoxelsFactory.reuseByte(voxels, binaryValues));
    }
}
