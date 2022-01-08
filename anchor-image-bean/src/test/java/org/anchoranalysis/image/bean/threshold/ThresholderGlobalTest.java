/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.threshold;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsBoundingBox;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ThresholderGlobalTest {

    private static final int SCENE_WIDTH = 4000;
    private static final int SCENE_HEIGHT = 7000;
    private static final int SCENE_DEPTH = 8;

    private static final Extent SCENE_EXTENT = new Extent(SCENE_WIDTH, SCENE_HEIGHT, SCENE_DEPTH);

    private static final Extent MASK_EXTENT = new Extent(30, 20, 4);
    private static final Point3i MASK_CORNER_MIN = new Point3i(10, 10, 2);

    private static final long HALF_SCENE_EXTENT_VOLUME = SCENE_EXTENT.calculateVolume() / 2;

    private VoxelsUntyped voxels;

    @BeforeEach
    void setup() {
        voxels = createVoxels();
    }

    @Test
    void testNoMask() throws OperationFailedException {
        testThreshold(Optional.empty(), HALF_SCENE_EXTENT_VOLUME, HALF_SCENE_EXTENT_VOLUME);
    }

    @Test
    void testMask() throws OperationFailedException {

        // An object-mask in the left half
        ObjectMask object = new ObjectMask(BoundingBox.createReuse(MASK_CORNER_MIN, MASK_EXTENT));
        object.assignOn().toAll();

        testThreshold(Optional.of(object), 2400, 112000000);
    }

    private void testThreshold(
            Optional<ObjectMask> object, long expectedCountOn, long expectedCountOff)
            throws OperationFailedException {
        Thresholder thresholder = createThresholder();

        BinaryVoxels<UnsignedByteBuffer> out =
                thresholder.threshold(
                        voxels, BinaryValuesByte.getDefault(), Optional.empty(), object);

        assertEquals(expectedCountOn, out.countOn(), "onCount");
        assertEquals(expectedCountOff, out.countOff(), "offCount");
    }

    private Thresholder createThresholder() {
        ThresholderGlobal thresholder = new ThresholderGlobal();
        thresholder.setCalculateLevel(createCalculateLevel());
        return thresholder;
    }

    private static CalculateLevel createCalculateLevel() {
        CalculateLevel calculateLevel = mock(CalculateLevel.class);
        try {
            when(calculateLevel.calculateLevel(any())).thenReturn(100);
        } catch (OperationFailedException e) {
        }
        return calculateLevel;
    }

    private static VoxelsUntyped createVoxels() {

        Extent extentHalf = new Extent(SCENE_WIDTH / 2, SCENE_HEIGHT, SCENE_DEPTH);

        Voxels<UnsignedByteBuffer> voxels =
                VoxelsFactory.getUnsignedByte().createInitialized(SCENE_EXTENT);

        BoundingBox left = BoundingBox.createReuse(new Point3i(0, 0, 0), extentHalf);
        BoundingBox right = BoundingBox.createReuse(new Point3i(SCENE_WIDTH / 2, 0, 0), extentHalf);

        writeModulo(voxels, left, 0);
        writeModulo(
                voxels, right,
                100); // So the right half, should be 100 higher on average, and always >= 100

        return new VoxelsUntyped(voxels);
    }

    private static void writeModulo(
            Voxels<UnsignedByteBuffer> voxels, BoundingBox box, int addToPixels) {
        IterateVoxelsBoundingBox.withBuffer(
                box,
                voxels,
                (Point3i point, UnsignedByteBuffer buffer, int offset) ->
                        buffer.putUnsigned(
                                offset, (point.y() % 50 + point.x() % 50) + addToPixels));
    }
}
