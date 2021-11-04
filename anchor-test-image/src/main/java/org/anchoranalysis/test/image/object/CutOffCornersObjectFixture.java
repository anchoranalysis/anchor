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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesInt;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Creates one or more objects that are otherwise rectangles with <i>on</i> values, but with corner
 * voxels as <i>off</i>.
 *
 * @author Owen Feehan
 */
public class CutOffCornersObjectFixture {

    /** If defined, a check occurs to make sure objects fit inside these dimensions. */
    private final Optional<Dimensions> dimensions;

    /** Create <b>without</b> any check on dimensions. */
    public CutOffCornersObjectFixture() {
        this.dimensions = Optional.empty();
    }

    /**
     * Create <b>with</b> any check on dimensions.
     *
     * @param dimensions a check occurs to make sure objects fit inside these dimensions.
     */
    public CutOffCornersObjectFixture(Dimensions dimensions) {
        this.dimensions = Optional.of(dimensions);
    }

    /**
     * Create the <b>first</b> object.
     *
     * @return a newly created object.
     */
    public ObjectMask create1() {
        return create(new Point3i(10, 15, 3), new Extent(20, 34, 11), 3, 2);
    }

    /**
     * Create the <b>second</b> object.
     *
     * @return a newly created object.
     */
    public ObjectMask create2() {
        return create(new Point3i(3, 1, 7), new Extent(19, 14, 5), 5, 1);
    }

    /**
     * Create the <b>third</b> object.
     *
     * @return a newly created object.
     */
    public ObjectMask create3() {
        return create(new Point3i(17, 15, 2), new Extent(19, 14, 13), 1, 5);
    }

    /**
     * Creates <b>all three</b> objects.
     *
     * @return collection of newly created objects.
     */
    public ObjectCollection createAll() {
        return ObjectCollectionFactory.of(create1(), create2(), create3());
    }

    private ObjectMask create(Point3i corner, Extent extent, int cornerEdgeXY, int cornerEdgeZ) {
        CutOffCorners pattern = new CutOffCorners(cornerEdgeXY, cornerEdgeZ, extent);
        return createAt(corner, extent, pattern);
    }

    private ObjectMask createAt(Point3i cornerMin, Extent extent, VoxelPattern pattern) {
        BoundingBox box = new BoundingBox(cornerMin, extent);

        if (dimensions.isPresent()) {
            assertTrue(dimensions.get().contains(box));
        }

        Voxels<UnsignedByteBuffer> voxels =
                VoxelsFactory.getUnsignedByte().createInitialized(extent);
        BinaryValuesInt binaryValues = BinaryValuesInt.getDefault();
        BinaryValuesByte binaryValuesByte = binaryValues.asByte();

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
