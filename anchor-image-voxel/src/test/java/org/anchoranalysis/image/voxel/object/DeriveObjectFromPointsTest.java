/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link DeriveObjectFromPoints}.
 *
 * @author Owen Feehan
 */
class DeriveObjectFromPointsTest {

    private static final Point3i POINT1 = new Point3i(7, 8, 9);
    private static final Point3i POINT2 = new Point3i(17, 8, 4);
    private static final Point3i POINT3 = new Point3i(27, 6, 29);

    private static final BoundingBox EXPECTED_BOUNDING_BOX =
            BoundingBox.createReuse(new Point3i(7, 6, 4), new Extent(21, 3, 26));

    private DeriveObjectFromPoints deriver = new DeriveObjectFromPoints();

    @Test
    void testWithAddedPoints() {

        deriver.add(POINT1);
        deriver.add(POINT2);
        deriver.add(POINT3);

        Optional<ObjectMask> objectMask = deriver.deriveObject();

        assertTrue(objectMask.isPresent());
        assertEquals(EXPECTED_BOUNDING_BOX, objectMask.get().boundingBox(), "boundingBox");
        assertEquals(3, objectMask.get().voxelsOn().count(), "number of on voxels");
    }

    @Test
    void testWithoutAddedPoints() {
        assertTrue(!deriver.deriveObject().isPresent());
    }
}
