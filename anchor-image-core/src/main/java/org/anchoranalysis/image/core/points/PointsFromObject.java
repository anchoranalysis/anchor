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

package org.anchoranalysis.image.core.points;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.contour.FindContour;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.point.Point2i;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Derives collections of points from an {@link ObjectMask} or its contour.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointsFromObject {

    /**
     * A list of two-dimensional integer points from the entire object-mask.
     *
     * @param object the object.
     * @return a newly-created list.
     * @throws CreateException if the object is in three-dimensions.
     */
    public static List<Point2i> listFrom2i(ObjectMask object) throws CreateException {
        return PointsFromVoxels.listFrom2i(object.binaryVoxels(), object.boundingBox().cornerMin());
    }

    /**
     * A list of three-dimensional integer points from the entire object-mask.
     *
     * @param object the object.
     * @return a newly-created list.
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
     * A list of points from the entire object-mask as {@link Point3d}.
     *
     * @param object the object.
     * @return a newly created list.
     */
    public static List<Point3d> listFrom3d(ObjectMask object) {
        return PointsFromVoxels.listFrom3d(object.binaryVoxels(), object.boundingBox().cornerMin());
    }

    /**
     * A list of points as {@link Point3i} from the outline of an object-mask.
     *
     * @param object the object.
     * @return a newly created list.
     */
    public static List<Point3i> listFromOutline3i(ObjectMask object) {
        return listFrom3i(contourFor(object, true));
    }

    /**
     * A list of points as three-dimensional integers from the outline of an object-mask
     *
     * @param object the object.
     * @return a newly created list.
     * @throws CreateException if the object is in three-dimensions
     */
    public static List<Point2i> listFromOutline2i(ObjectMask object) throws CreateException {
        return listFrom2i(contourFor(object, false));
    }

    /**
     * A list of points as three-dimensional integers from the outline of all objects in a
     * collection.
     *
     * @param objects objects to find outlines for.
     * @return a newly created list.
     */
    public static List<Point2i> listFromAllOutlines2i(ObjectCollection objects) {
        List<Point2i> points = new ArrayList<>();

        for (ObjectMask object : objects) {
            consumeContourPoints(object, points::add);
        }

        return points;
    }

    /**
     * A set of points from the contour of {@code object} in three dimensions.
     *
     * @param object the object.
     * @return a newly created set of points, encoded as {@link Point3i}.
     */
    public static Set<Point3i> setFromContour(ObjectMask object) {
        return setFrom3i(contourFor(object, false));
    }

    /**
     * Calculates the points on the contour of {@code object} and consumes each of them as {@link
     * Point3i}.
     */
    private static void consumeContourPoints(ObjectMask object, Consumer<Point2i> consumer) {
        ObjectMask outline = contourFor(object, false);
        PointsFromVoxels.consumePoints2i(
                outline.binaryVoxels(), outline.boundingBox().cornerMin(), consumer);
    }

    /** Finds the contour of an {@code object}, 1 voxel deep. */
    private static ObjectMask contourFor(ObjectMask object, boolean do3D) {
        return FindContour.createFrom(object, 1, do3D, false);
    }
}
