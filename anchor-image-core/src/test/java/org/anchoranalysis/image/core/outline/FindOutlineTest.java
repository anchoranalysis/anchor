/*-
 * #%L
 * anchor-image-core
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
package org.anchoranalysis.image.core.outline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link FindOutline}.
 *
 * @author Owen Feehan
 */
class FindOutlineTest {

    /** Tests a 2D object with a outline-size of 1. */
    @Test
    void testSingleWidthObject2D() {
        assertOutline(false, 1);
    }

    /** Tests a 2D object with a outline-size of 2. */
    @Test
    void testDoubleWidthObject2D() {
        assertOutline(false, 2);
    }

    /** Tests a 3D object with a outline-size of 1. */
    @Test
    void testSingleWidthObject3D() {
        assertOutline(true, 1);
    }

    private void assertOutline(boolean useZ, int outlineSize) {
        ObjectMask object = RectangleObjectFixture.create(useZ);

        int objectNumberVoxels = expectedNumberVoxelsEntire(useZ);
        assertNumberVoxels("entire object (before)", objectNumberVoxels, object);

        ObjectMask outline = FindOutline.outline(object, outlineSize, useZ, true);

        assertNumberVoxels("entire object (after)", objectNumberVoxels, object);

        assertNumberVoxels("outline", expectedNumberVoxelsObject(useZ, outlineSize), outline);
    }

    /** The expected number of voxels for the entire object before being outlined. */
    private static int expectedNumberVoxelsEntire(boolean use3D) {
        return RectangleObjectFixture.RECTANGLE_WIDTH
                * RectangleObjectFixture.RECTANGLE_HEIGHT
                * (use3D ? RectangleObjectFixture.RECTANGLE_DEPTH : 1);
    }

    /** The expected number of voxels for the outline found for the object. */
    private static int expectedNumberVoxelsObject(boolean use3D, int outlineSize) {
        if (use3D) {
            if (outlineSize == 1) {
                return numberVoxelsOutline3D();
            } else {
                throw new AnchorFriendlyRuntimeException(
                        "If use3D==true, then only outlineSize==1 is supported in the tests");
            }
        } else {
            return numberVoxelsOutline2D(outlineSize);
        }
    }

    /** The number of voxels on the outline of a 3D-object */
    private static int numberVoxelsOutline3D() {
        // In 3D case, total outline is two full slices of voxels, and the rest are partial-slices
        int partialSlice =
                numberVoxelsOutlineSlice(
                        RectangleObjectFixture.RECTANGLE_WIDTH,
                        RectangleObjectFixture.RECTANGLE_HEIGHT);
        int fullSlice =
                RectangleObjectFixture.RECTANGLE_WIDTH * RectangleObjectFixture.RECTANGLE_HEIGHT;
        return (fullSlice * 2) + (partialSlice * (RectangleObjectFixture.RECTANGLE_DEPTH - 2));
    }

    private static int numberVoxelsOutline2D(int outlineSize) {
        int sum = 0;
        for (int i = 0; i < outlineSize; i++) {
            int subtract = i * 2;
            sum +=
                    numberVoxelsOutlineSlice(
                            RectangleObjectFixture.RECTANGLE_WIDTH - subtract,
                            RectangleObjectFixture.RECTANGLE_HEIGHT - subtract);
        }
        return sum;
    }

    /** The number of voxels on the outline of a single z-slice. */
    private static int numberVoxelsOutlineSlice(int width, int height) {
        return (width * 2) + (height * 2) - 4;
    }

    private static void assertNumberVoxels(
            String messagePrefix, int expectedNumberVoxels, ObjectMask object) {
        assertEquals(
                expectedNumberVoxels,
                object.numberVoxelsOn(),
                () -> messagePrefix + " - number of voxels");
    }
}
