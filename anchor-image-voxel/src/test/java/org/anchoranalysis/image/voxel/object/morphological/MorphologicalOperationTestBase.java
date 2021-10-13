/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.object.morphological;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.junit.jupiter.api.Test;

/**
 * Base class for testing morphological operations on an {@link ObjectMask}.
 *
 * <p>The {@link ObjectMask} to be tested is always a rectangular-object (or cuboid in 3D) that
 * occupies all of a scene apart from a single-pixel border.
 *
 * @author Owen Feehan
 */
abstract class MorphologicalOperationTestBase {

    @Test
    void test2D() throws CreateException {
        test(false);
    }

    @Test
    void test3D() throws CreateException {
        test(true);
    }

    /**
     * Applies a particular morphological operation to an {@link ObjectMask}.
     *
     * @param object the object to apply the operation to
     * @param fixture the fixture that created the object
     * @param sceneExtent the size of the scene
     * @param useZ whether the kernel was applied in the z-dimension or not
     * @return the object-mask after the operation.
     * @throws CreateException
     */
    protected abstract ObjectMask applyOperation(
            ObjectMask object, ObjectMaskFixture fixture, Extent sceneExtent, boolean useZ)
            throws CreateException;

    /**
     * An addition to the number of voxels on the {@link ObjectMask} <i>before</i> the morphological
     * operation to determine the expected number <i>after</i>.
     *
     * @param fixture the fixture
     * @param useZ whether the kernel was applied in the z-dimension or not
     * @return a positive or negative number that will be added to the number of voxels before the
     *     morphological operation.
     */
    protected abstract int expectedChangeNumberVoxels(ObjectMaskFixture fixture, boolean useZ);

    private void test(boolean do3D) throws CreateException {
        ObjectMaskFixture fixture = new ObjectMaskFixture(false, do3D);

        Extent sceneExtent = fixture.extent().growBy(2);

        // Create object and apply erosion
        ObjectMask before = fixture.filledMask(cornerPoint(do3D));

        ObjectMask after = applyOperation(before, fixture, sceneExtent, do3D);

        assertExpectedNumberVoxels(before, after, fixture, do3D);
    }

    private static Point3i cornerPoint(boolean do3D) {
        return new Point3i(1, 1, do3D ? 1 : 0);
    }

    private void assertExpectedNumberVoxels(
            ObjectMask before, ObjectMask after, ObjectMaskFixture fixture, boolean useZ) {
        int expectedNumberVoxels =
                before.numberVoxelsOn() + expectedChangeNumberVoxels(fixture, useZ);
        assertEquals(expectedNumberVoxels, after.numberVoxelsOn());
    }
}
