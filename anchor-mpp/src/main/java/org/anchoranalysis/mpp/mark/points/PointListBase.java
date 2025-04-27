/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.mark.points;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3d;
import org.apache.commons.collections.ListUtils;

/** A base class for marks that consist of a list of 3D points. */
public abstract class PointListBase extends Mark {

    private static final long serialVersionUID = 6520431317406007141L;

    /** The list of 3D points that make up this mark. */
    @Getter private List<Point3d> points;

    /** The minimum x, y, z coordinates of all the points in the list. */
    @Getter private Point3d min;

    /** The maximum x, y, z coordinates of all the points in the list. */
    @Getter private Point3d max;

    /** Constructs an empty PointListBase. */
    protected PointListBase() {
        points = new ArrayList<>();
    }

    /**
     * Constructs a PointListBase from a stream of Point3d objects.
     *
     * @param stream the stream of Point3d objects
     */
    protected PointListBase(Stream<Point3d> stream) {
        points = stream.toList();
        updateAfterPointsChange();
    }

    /**
     * Duplicates this PointListBase into a new instance.
     *
     * @param markNew the new PointListBase instance to copy into
     */
    protected void doDuplicate(PointListBase markNew) {
        markNew.points = new ArrayList<>();
        getPoints().forEach(markNew.points::add);
        markNew.setId(getIdentifier());
        markNew.updateAfterPointsChange();
    }

    /** Updates the min and max points after the list of points has changed. */
    public void updateAfterPointsChange() {
        assert (!points.isEmpty());
        this.min = calculateMin(getPoints());
        this.max = calculateMax(getPoints());
    }

    @Override
    public BoundingBox box(Dimensions dimensions, int regionID) {
        // Note that the shell radius is ignored.
        return BoundingBox.createReuse(min, max);
    }

    /**
     * Calculates the minimum point from a list of points.
     *
     * @param points the list of points
     * @return the minimum point
     */
    private static Point3d calculateMin(List<Point3d> points) {
        Point3d min =
                new Point3d(
                        Double.POSITIVE_INFINITY,
                        Double.POSITIVE_INFINITY,
                        Double.POSITIVE_INFINITY);
        for (Point3d point : points) {
            if (point.x() < min.x()) {
                min.setX(point.x());
            }

            if (point.y() < min.y()) {
                min.setY(point.y());
            }

            if (point.z() < min.z()) {
                min.setZ(point.z());
            }
        }
        return min;
    }

    /**
     * Calculates the maximum point from a list of points.
     *
     * @param points the list of points
     * @return the maximum point
     */
    private static Point3d calculateMax(List<Point3d> points) {
        Point3d max =
                new Point3d(
                        Double.NEGATIVE_INFINITY,
                        Double.NEGATIVE_INFINITY,
                        Double.NEGATIVE_INFINITY);
        for (Point3d point : points) {
            if (point.x() > max.x()) {
                max.setX(point.x());
            }

            if (point.y() > max.y()) {
                max.setY(point.y());
            }

            if (point.z() > max.z()) {
                max.setZ(point.z());
            }
        }
        return max;
    }

    /**
     * Creates a bounding box from the min and max points.
     *
     * @return the bounding box
     */
    protected BoundingBox box() {
        return BoundingBox.createReuse(getMin(), getMax());
    }

    @Override
    public boolean equalsDeep(Mark mark) {

        if (mark == null) {
            return false;
        }
        if (mark == this) {
            return true;
        }

        if (!super.equalsDeep(mark)) {
            return false;
        }

        if (mark instanceof PointListBase markCast) {

            if (min != markCast.getMin()) {
                return false;
            }

            if (max != markCast.getMax()) {
                return false;
            }

            return ListUtils.isEqualList(points, markCast.getPoints());
        } else {
            return false;
        }
    }
}
