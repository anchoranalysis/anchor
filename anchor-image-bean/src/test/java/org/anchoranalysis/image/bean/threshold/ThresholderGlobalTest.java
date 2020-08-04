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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.iterator.IterateVoxels;
import org.junit.Before;
import org.junit.Test;

public class ThresholderGlobalTest {

    private static final int SCENE_WIDTH = 4000;
    private static final int SCENE_HEIGHT = 7000;
    private static final int SCENE_DEPTH = 8;

    private static final Extent SCENE_EXTENT = new Extent(SCENE_WIDTH, SCENE_HEIGHT, SCENE_DEPTH);

    private static final Extent MASK_EXTENT = new Extent(30, 20, 4);
    private static final Point3i MASK_CORNER_MIN = new Point3i(10, 10, 2);

    private static final long HALF_SCENE_EXTENT_VOLUME = SCENE_EXTENT.getVolume() / 2;

    private VoxelBoxWrapper voxelBox;

    @Before
    public void setup() {
        voxelBox = createVoxels();
    }

    @Test
    public void testNoMask() throws OperationFailedException {
        testThreshold(Optional.empty(), HALF_SCENE_EXTENT_VOLUME, HALF_SCENE_EXTENT_VOLUME);
    }

    @Test
    public void testMask() throws OperationFailedException {

        // An object-mask in the left half
        ObjectMask object = new ObjectMask(new BoundingBox(MASK_CORNER_MIN, MASK_EXTENT));
        object.binaryVoxels().setAllPixelsToOn();

        testThreshold(Optional.of(object), 2400, 112000000);
    }

    private void testThreshold(
            Optional<ObjectMask> object, long expectedCountOn, long expectedCountOff)
            throws OperationFailedException {
        Thresholder thresholder = createThresholder();

        BinaryVoxelBox<ByteBuffer> out =
                thresholder.threshold(
                        voxelBox, BinaryValuesByte.getDefault(), Optional.empty(), object);

        assertEquals("onCount", expectedCountOn, out.countOn());
        assertEquals("offCount", expectedCountOff, out.countOff());
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

    private static VoxelBoxWrapper createVoxels() {

        Extent extentHalf = new Extent(SCENE_WIDTH / 2, SCENE_HEIGHT, SCENE_DEPTH);

        VoxelBox<ByteBuffer> vb = VoxelBoxFactory.getByte().create(SCENE_EXTENT);

        BoundingBox left = new BoundingBox(new Point3i(0, 0, 0), extentHalf);
        BoundingBox right = new BoundingBox(new Point3i(SCENE_WIDTH / 2, 0, 0), extentHalf);

        writeModulo(vb, left, 0);
        writeModulo(
                vb, right,
                100); // So the right half, should be 100 higher on average, and always >= 100

        return new VoxelBoxWrapper(vb);
    }

    private static void writeModulo(VoxelBox<ByteBuffer> vb, BoundingBox bbox, int addToPixels) {
        IterateVoxels.callEachPoint(
                vb,
                bbox,
                (Point3i point, ByteBuffer buffer, int offset) ->
                        buffer.put(
                                offset,
                                (byte) ((point.getY() % 50 + point.getX() % 50) + addToPixels)));
    }
}
