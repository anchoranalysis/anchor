/*-
 * #%L
 * anchor-image-core
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
package org.anchoranalysis.image.core.object.scale.method;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/** Creates two rectangular objects, side-by-side. */
class ObjectFixture {

    public static final int WIDTH = 50;

    /** The left-most object that is included in every collection. */
    public static final ObjectMask OBJECT_LEFT = objectAt(0);

    /*** The right-most object that is included only in the <i>adjacent</i> collection. */
    public static final ObjectMask OBJECT_RIGHT_ADJACENT = objectAt(WIDTH);

    /*** The right-most object that is included only in the <i>overlapping</i> collection. */
    public static final ObjectMask OBJECT_RIGHT_OVERLAP = objectAt(WIDTH / 2);

    /**
     * Create a rectangular image where the first half is one object, and the second half is another
     * - without overlap.
     *
     * @return the objects.
     */
    public static List<ObjectMask> objectsAdjacent() {
        return Arrays.asList(OBJECT_LEFT, OBJECT_RIGHT_ADJACENT);
    }

    /**
     * Create a rectangular image where the second object overlaps with the first.
     *
     * @return the objects.
     */
    public static List<ObjectMask> objectsOverlap() {
        return Arrays.asList(OBJECT_LEFT, OBJECT_RIGHT_OVERLAP);
    }

    private static ObjectMask objectAt(int xMin) {
        ObjectMask object =
                new ObjectMask(BoundingBox.createReuse(new Point3i(xMin, 0, 0), new Extent(WIDTH, 30, 10)));
        object.assignOn().toAll();
        return object;
    }
}
