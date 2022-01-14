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

package org.anchoranalysis.spatial.box;

import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Does a bounding box contain other objects?
 *
 * <p>The other objects can be points, other bounding boxes etc.
 *
 * @author Owen Feehan
 */
public final class BoundingBoxContains {

    private final BoundingBox box;
    private final ReadableTuple3i cornerMax;

    /**
     * Create with the {@link BoundingBox} that is queried.
     *
     * @param box the bounding-box to query whether other entities are contained within it.
     */
    public BoundingBoxContains(BoundingBox box) {
        this.box = box;
        this.cornerMax = box.calculateCornerMaxInclusive();
    }

    /**
     * Is this value in the X-dimension within the bounding box range?
     *
     * @param value a value to be tested whether it lies within the bounding-box on the X-axis.
     * @return true iff the value is within the range.
     */
    public boolean x(int value) {
        return (value >= box.cornerMin().x()) && (value <= cornerMax.x());
    }

    /**
     * Is this value in the Y-dimension within the bounding box range?
     *
     * @param value a value to be tested whether it lies within the bounding-box on the Y-axis.
     * @return true iff the value is within the range.
     */
    public boolean y(int value) {
        return (value >= box.cornerMin().y()) && (value <= cornerMax.y());
    }

    /**
     * Is this value in the Z-dimension within the bounding box range?
     *
     * @param value a value to be tested whether it lies within the bounding-box on the Z-axis.
     * @return true iff the value is within the range.
     */
    public boolean z(int value) {
        return (value >= box.cornerMin().z()) && (value <= cornerMax.z());
    }

    /**
     * Is this point within the bounding-box?
     *
     * @param point point to test whether it lies within the bounding-box.
     * @return true iff the point lies inside the bounding-box.
     */
    public boolean point(ReadableTuple3i point) {
        return x(point.x()) && y(point.y()) && z(point.z());
    }

    /**
     * Like {@link #point(ReadableTuple3i)} but ignores the z-dimension.
     *
     * @param point point to test whether it lies within the bounding-box.
     * @return true iff the point lies inside the bounding-box, disregarding the z-dimension.
     */
    public boolean pointIgnoreZ(Point3i point) {
        return x(point.x()) && y(point.y());
    }

    /**
     * Is this other bounding-box <i>fully</i> contained within this bounding box?
     *
     * @param maybeContainedInside box to test whether it is contained inside or not.
     * @return true iff the {@code maybeContainedInside} entirely lies inside the bounding-box.
     */
    public boolean box(BoundingBox maybeContainedInside) {
        return point(maybeContainedInside.cornerMin())
                && point(maybeContainedInside.calculateCornerMaxInclusive());
    }
}
