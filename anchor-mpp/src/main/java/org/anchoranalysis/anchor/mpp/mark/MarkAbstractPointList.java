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

package org.anchoranalysis.anchor.mpp.mark;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.apache.commons.collections.ListUtils;

public abstract class MarkAbstractPointList extends Mark {

    /** */
    private static final long serialVersionUID = 6520431317406007141L;

    @Getter private List<Point3d> points;

    @Getter private Point3d min; // Contains the minimum x, y of all the points in the polygon

    @Getter private Point3d max; // Contains the maximum x, y of all the points in the polygon

    public MarkAbstractPointList() {
        points = new ArrayList<>();
    }

    public MarkAbstractPointList(Stream<Point3d> stream) {
        points = stream.collect(Collectors.toList());
        updateAfterPointsChange();
    }

    protected void doDuplicate(MarkAbstractPointList markNew) {
        markNew.points = new ArrayList<>();
        getPoints().forEach(markNew.points::add);
        markNew.setId(getId());
        markNew.updateAfterPointsChange();
    }

    public void updateAfterPointsChange() {
        assert (!points.isEmpty());

        this.min = calcMin(getPoints());
        this.max = calcMax(getPoints());
    }

    @Override
    public BoundingBox box(ImageDimensions bndScene, int regionID) {
        // TODO FOR NOW WE IGNORE THE SHELL RADIUS
        return new BoundingBox(min, max);
    }

    private static Point3d calcMin(List<Point3d> points) {
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

    private static Point3d calcMax(List<Point3d> points) {
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

    protected BoundingBox box() {
        return new BoundingBox(getMin(), getMax());
    }

    @Override
    public boolean equalsDeep(Mark m) {

        if (m == null) {
            return false;
        }
        if (m == this) {
            return true;
        }

        if (!super.equalsDeep(m)) {
            return false;
        }

        if (m instanceof MarkAbstractPointList) {
            MarkAbstractPointList objCast = (MarkAbstractPointList) m;

            if (min != objCast.getMin()) {
                return false;
            }

            if (max != objCast.getMax()) {
                return false;
            }

            return ListUtils.isEqualList(points, objCast.getPoints());
        } else {
            return false;
        }
    }
}
