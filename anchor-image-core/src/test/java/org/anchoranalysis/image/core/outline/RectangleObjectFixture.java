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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Creates a rectangular object that doesn't lie at the edges.
 *
 * <p>A margin is left on each side of a certain size.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RectangleObjectFixture {

    /** Width of scene. */
    private static final int SCENE_WIDTH = 20;

    /** Height of scene. */
    private static final int SCENE_HEIGHT = 15;

    /** Depth of scene. */
    private static final int SCENE_DEPTH = 7;

    /** The space between the scene and the rectangle in all dimensions. */
    private static final int MARGIN = 2;

    /** Width of rectangle. */
    public static final int RECTANGLE_WIDTH = subtractTwoMargins(SCENE_WIDTH);

    /** Height of rectangle. */
    public static final int RECTANGLE_HEIGHT = subtractTwoMargins(SCENE_HEIGHT);

    /** Depth of rectangle. */
    public static final int RECTANGLE_DEPTH = subtractTwoMargins(SCENE_DEPTH);

    public static ObjectMask create(boolean useZ) {
        Point3i corner = new Point3i(MARGIN, MARGIN, useZ ? MARGIN : 1);
        Extent reducedExtent =
                new Extent(
                        subtractTwoMargins(SCENE_WIDTH),
                        subtractTwoMargins(SCENE_HEIGHT),
                        useZ ? subtractTwoMargins(SCENE_DEPTH) : 1);
        return createRectangle(corner, reducedExtent);
    }

    private static int subtractTwoMargins(int fullExtent) {
        return fullExtent - (2 * MARGIN);
    }

    private static ObjectMask createRectangle(Point3i corner, Extent extent) {
        ObjectMask object = new ObjectMask(new BoundingBox(corner, extent));
        object.assignOn().toAll();
        return object;
    }
}
