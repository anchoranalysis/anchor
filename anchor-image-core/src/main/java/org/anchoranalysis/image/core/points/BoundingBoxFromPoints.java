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

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.PointRange;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Creates a bounding-box from one or more points
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoundingBoxFromPoints {

    /**
     * Create from a list of points
     *
     * @param points the list
     * @return a bounding-box minimally spanning all points
     * @throws OperationFailedException if there are zero points
     */
    public static BoundingBox forList(List<Point3i> points) throws OperationFailedException {
        if (points.isEmpty()) {
            throw new OperationFailedException("Points list must contain at least one item");
        }

        PointRange range = new PointRange();
        points.forEach(range::add);

        return range.toBoundingBox();
    }

    /**
     * Creates a bounding-box for two unordered points.
     *
     * <p>By unordered, it means that no one point must have a value higher than another
     *
     * @param point1 first-point (arbitrary order)
     * @param point2 second-point (arbitrary order)
     * @return a bounding-box minally spanning the two points
     */
    public static BoundingBox forTwoPoints(Point3d point1, Point3d point2) {
        Point3d min = calculateMin(point1, point2);
        Point3d max = calculateMax(point1, point2);
        return new BoundingBox(min, max);
    }

    private static Point3d calculateMin(Point3d point1, Point3d point2) {
        Point3d point = new Point3d();
        point.setX(Math.min(point1.x(), point2.x()));
        point.setY(Math.min(point1.y(), point2.y()));
        point.setZ(Math.min(point1.z(), point2.z()));
        return point;
    }

    private static Point3d calculateMax(Point3d point1, Point3d point2) {
        Point3d point = new Point3d();
        point.setX(Math.max(point1.x(), point2.x()));
        point.setY(Math.max(point1.y(), point2.y()));
        point.setZ(Math.max(point1.z(), point2.z()));
        return point;
    }
}
