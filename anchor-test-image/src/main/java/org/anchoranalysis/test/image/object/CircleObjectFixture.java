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
package org.anchoranalysis.test.image.object;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsBoundingBox;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point2d;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.PointConverter;

/**
 * Creates object-masks that are circles in different ways
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CircleObjectFixture {

    /**
     * Creates several successive circles whose center is incrementally shifted.
     *
     * @param numberCircles how many circles to create.
     * @param startCenter the center of the first circle.
     * @param radius the radius for all circles.
     * @param centerShift shifts the center by this amount for each successive circle.
     * @param radiusShift increases the radius by this amount for each successive circle.
     * @return a collection of circular-objects.
     */
    public static ObjectCollection successiveCircles(
            int numberCircles,
            Point2d startCenter,
            int radius,
            Point2d centerShift,
            double radiusShift) {

        ObjectCollection out = new ObjectCollection();

        Point2d currentCenter = startCenter;
        double currentRadius = radius;

        for (int i = 0; i < numberCircles; i++) {
            out.add(circleAt(currentCenter, (int) currentRadius));

            currentCenter.add(centerShift);
            currentRadius += radiusShift;
        }

        return out;
    }

    /**
     * Creates a single circular mask at a particular center point and radius.
     *
     * @param center center-point.
     * @param radius the radius.
     * @return a circular object-mask as above with a minimally fitting bounding-box around it.
     */
    public static ObjectMask circleAt(Point2d center, int radius) {
        Point3d center3d = PointConverter.convertTo3d(center);

        BoundingBox box = boundingBoxForCircle(center3d, radius);

        double radiusSquared = Math.pow(radius, 2.0);

        ObjectMask object = new ObjectMask(box);
        VoxelsAssigner assigner = object.assignOn();
        IterateVoxelsBoundingBox.withMatchingPoints(
                box, point -> center3d.distanceSquared(point) <= radiusSquared, assigner::toVoxel);
        return object;
    }

    private static BoundingBox boundingBoxForCircle(Point3d center3d, int radius) {

        Point3i centerCeil = PointConverter.intFromDoubleCeil(center3d);
        Point3i centerFloor = PointConverter.intFromDoubleFloor(center3d);
        Point3i radii = new Point3i(radius, radius, 0);
        centerFloor.subtract(radii);
        centerCeil.add(radii);
        return BoundingBox.createReuse(centerFloor, centerCeil);
    }
}
