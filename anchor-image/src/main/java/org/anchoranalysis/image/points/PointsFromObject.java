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

package org.anchoranalysis.image.points;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.outline.FindOutline;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointsFromObject {

    /**
     * A list of two-dimensional integer points from the entire object-mask
     *
     * @param object the object
     * @return a newly-created list
     * @throws CreateException if the object is in three-dimensions
     */
    public static List<Point2i> listFrom2i(ObjectMask object) throws CreateException {
        return PointsFromVoxels.listFrom2i(object.binaryVoxels(), object.boundingBox().cornerMin());
    }

    /**
     * A list of three-dimensional integer points from the entire object-mask
     *
     * @param object the object
     * @return a newly-created list
     */
    public static List<Point3i> listFrom3i(ObjectMask object) {
        return PointsFromVoxels.listFrom3i(object.binaryVoxels(), object.boundingBox().cornerMin());
    }

    /**
     * A sorted-set of three-dimensional integer points from the entire object-mask
     *
     * @param object the object
     * @return a newly-created list
     */
    public static SortedSet<Point3i> setFrom3i(ObjectMask object) {
        return PointsFromVoxels.setFrom3i(object.binaryVoxels(), object.boundingBox().cornerMin());
    }

    /**
     * A list of points from the entire object-mask
     *
     * @param object
     * @return
     */
    public static List<Point3d> listFrom3d(ObjectMask object) {
        return PointsFromVoxels.listFrom3d(object.binaryVoxels(), object.boundingBox().cornerMin());
    }

    /**
     * A list of points as three-dimensional integers from the outline of an object-mask
     *
     * @param object
     * @return
     * @throws CreateException
     */
    public static List<Point3i> listFromOutline3i(ObjectMask object) {
        return listFrom3i(outlineFor(object, true));
    }

    /**
     * A list of points as three-dimensional integers from the outline of an object-mask
     *
     * @param object
     * @return
     * @throws CreateException if the object is in three-dimensions
     */
    public static List<Point2i> listFromOutline2i(ObjectMask object) throws CreateException {
        return listFrom2i(outlineFor(object, false));
    }

    /**
     * A list of points as three-dimensional integers from the outline of all objects in a
     * collection
     *
     * @param objects objects to find outlines for
     * @return a newly created list
     * @throws CreateException if the object is in three-dimensions
     */
    public static List<Point2i> listFromAllOutlines2i(ObjectCollection objects) {
        List<Point2i> points = new ArrayList<>();

        for (ObjectMask object : objects) {
            consumeOutline2i(object, points::add);
        }

        return points;
    }

    public static Set<Point3i> setFromOutline(ObjectMask object) {
        return setFrom3i(outlineFor(object, false));
    }

    private static ObjectMask outlineFor(ObjectMask object, boolean do3D) {
        return FindOutline.outline(object, 1, do3D, false);
    }

    private static void consumeOutline2i(ObjectMask object, Consumer<Point2i> consumer) {
        ObjectMask outline = outlineFor(object, false);
        PointsFromVoxels.consumePoints2i(
                outline.binaryVoxels(), outline.boundingBox().cornerMin(), consumer);
    }
}
