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

package org.anchoranalysis.image.extent;

import java.io.Serializable;
import java.util.function.ToDoubleFunction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.core.geometry.Tuple3i;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.scale.ScaleFactorUtilities;
import org.anchoranalysis.image.voxel.Voxels;

/**
 * A bounding-box in 2 or 3 dimensions
 *
 * <p>A 2D bounding-box should always have a z-extent of 1 pixel.
 *
 * <p>This is an <i>immutable</i> class. All operations are immutable (i.e. do not modify the state
 * of the existing object).
 */
@EqualsAndHashCode
@Accessors(fluent = true)
public final class BoundingBox implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    /** The bottom-left corner of the bounding box. */
    private final Point3i cornerMin;

    /** Dimensions in pixels needed to represent the bounding box */
    @Getter private final Extent extent;

    /**
     * Constructs a bounding-box to cover the entirety of certain dimensions
     *
     * @param dimensions the dimensions
     */
    public BoundingBox(Dimensions dimensions) {
        this(dimensions.extent());
    }

    /**
     * Constructs a bounding-box to cover the entirety of certain voxels
     *
     * @param voxels the voxels
     */
    public BoundingBox(Voxels<?> voxels) {
        this(voxels.extent());
    }

    /**
     * Constructs a bounding-box to cover the entirety of a certain extent
     *
     * @param extent the extent
     */
    public BoundingBox(Extent extent) {
        this(new Point3i(), extent);
    }

    /**
     * Constructor - creates a new bounding-box from two points (a minimum corner and a maximum
     * corner)
     *
     * @param cornerMinInclusive minimum point in each dimension of the bounding-box (that exists
     *     inside the box)
     * @param cornerMaxInclusive maximum point in each dimension of the bounding-box (that exists
     *     inside the box)
     */
    public BoundingBox(Point3d cornerMinInclusive, Point3d cornerMaxInclusive) {
        this(
                PointConverter.intFromDoubleFloor(cornerMinInclusive),
                PointConverter.intFromDoubleCeil(cornerMaxInclusive));
    }

    /**
     * Constructor - creates a new bounding-box from two points (a minimum corner and a maximum
     * corner)
     *
     * @param cornerMinInclusive minimum point in each dimension of the bounding-box (that exists
     *     inside the box)
     * @param cornerMaxInclusive maximum point in each dimension of the bounding-box (that exists
     *     inside the box)
     */
    public BoundingBox(ReadableTuple3i cornerMinInclusive, ReadableTuple3i cornerMaxInclusive) {
        this(
                cornerMinInclusive,
                new Extent(
                        cornerMaxInclusive.x() - cornerMinInclusive.x() + 1,
                        cornerMaxInclusive.y() - cornerMinInclusive.y() + 1,
                        cornerMaxInclusive.z() - cornerMinInclusive.z() + 1));
        checkMaxMoreThanMin(cornerMinInclusive, cornerMaxInclusive);
    }

    // Extent is the number of pixels need to represent this bounding box
    public BoundingBox(ReadableTuple3i cornerMin, Extent extent) {
        // Note this always duplicates the corner, creating some needless object-creation
        this.cornerMin = new Point3i(cornerMin);
        this.extent = extent;
    }

    /**
     * A mid-point in the bounding box, in the exact half way point between (crnr+extent)/2.
     *
     * <p>It may not be integral, and could end with .5
     *
     * @return the midpoint
     */
    public Point3d midpoint() {
        return meanOfExtent(0);
    }

    /**
     * Similar to {@link #midpoint} but not always identical. It is the mean of all the points in
     * the box, and guaranteed to be integral.
     *
     * <p>It should always be identical in each dimension to {@code (corner()+extent()-1)/2}
     *
     * @return the center-of-gravity
     */
    public Point3i centerOfGravity() {
        return PointConverter.intFromDoubleFloor(meanOfExtent(1));
    }

    public BoundingBox flattenZ() {
        return new BoundingBox(cornerMin.duplicateChangeZ(0), extent.duplicateChangeZ(1));
    }

    public BoundingBox changeExtent(Extent extent) {
        return new BoundingBox(cornerMin, extent);
    }

    public BoundingBox changeZ(int crnrZ, int extentZ) {
        return new BoundingBox(cornerMin.duplicateChangeZ(crnrZ), extent.duplicateChangeZ(extentZ));
    }

    public BoundingBox changeExtentZ(int extentZ) {
        return new BoundingBox(cornerMin, extent.duplicateChangeZ(extentZ));
    }

    public boolean atBorder(Dimensions dimensions) {

        if (atBorderXY(dimensions)) {
            return true;
        }

        return atBorderZ(dimensions);
    }

    public boolean atBorderXY(Dimensions dimensions) {

        ReadableTuple3i cornerMax = this.calculateCornerMaxExclusive();

        if (cornerMin.x() == 0) {
            return true;
        }
        if (cornerMin.y() == 0) {
            return true;
        }

        if (cornerMax.x() == dimensions.x()) {
            return true;
        }
        return cornerMax.y() == dimensions.y();
    }

    public boolean atBorderZ(Dimensions dimensions) {

        ReadableTuple3i cornerMax = this.calculateCornerMaxExclusive();

        if (cornerMin.z() == 0) {
            return true;
        }
        return cornerMax.z() == dimensions.z();
    }

    public BoundingBox growBy(Tuple3i toAdd, Extent containingExtent) {

        // Subtract the padding from the corner
        Point3i cornerMinShifted = Point3i.immutableSubtract(cornerMin, toAdd);

        // Add double-padding in each dimension to the extent
        Extent extentGrown = extent.growBy(multiplyByTwo(toAdd));

        // Clip to make sure we remain within bounds
        return new BoundingBox(cornerMinShifted, extentGrown).clipTo(containingExtent);
    }

    /**
     * The maximum (right-most) point <i>inside</i> the box.
     *
     * <p>This means that iterators should be {@code <= calculateCornerMax()}.
     *
     * @return the maximum point inside the box in each dimension
     */
    public ReadableTuple3i calculateCornerMax() {
        Point3i out = new Point3i();
        out.setX(cornerMin.x() + extent.x() - 1);
        out.setY(cornerMin.y() + extent.y() - 1);
        out.setZ(cornerMin.z() + extent.z() - 1);
        return out;
    }

    /**
     * The maximum (right-most) point just outside the box.
     *
     * <p>It is equivalent to {@code < calculateCornerMax()} plus {@code 1} in each dimension.
     *
     * <p>This means that iterators should be {@code < calculateCornerMaxExclusive()}.
     *
     * @return the maximum point inside the box in each dimension
     */
    public Point3i calculateCornerMaxExclusive() {
        Point3i out = new Point3i();
        out.setX(cornerMin.x() + extent.x());
        out.setY(cornerMin.y() + extent.y());
        out.setZ(cornerMin.z() + extent.z());
        return out;
    }

    public BoundingBox clipTo(Extent extent) {

        if (cornerMin.x() >= extent.x()
                || cornerMin.y() >= extent.y()
                || cornerMin.z() >= extent.z()) {
            throw new AnchorFriendlyRuntimeException(
                    String.format(
                            "Corner-min (%s) is outside the clipping region (%s)",
                            cornerMin, extent));
        }

        Point3i min = new Point3i(cornerMin);
        Point3i max = new Point3i(calculateCornerMax());

        if (min.x() < 0) {
            min.setX(0);
        }
        if (min.y() < 0) {
            min.setY(0);
        }
        if (min.z() < 0) {
            min.setZ(0);
        }

        if (max.x() >= extent.x()) {
            max.setX(extent.x() - 1);
        }
        if (max.y() >= extent.y()) {
            max.setY(extent.y() - 1);
        }
        if (max.z() >= extent.z()) {
            max.setZ(extent.z() - 1);
        }

        return new BoundingBox(min, max);
    }

    public Point3i closestPointOnBorder(Point3d pointIn) {

        ReadableTuple3i cornerMax = calculateCornerMax();

        Point3i pointOut = new Point3i();
        pointOut.setX(closestPointOnAxis(pointIn.x(), cornerMin.x(), cornerMax.x()));
        pointOut.setY(closestPointOnAxis(pointIn.y(), cornerMin.y(), cornerMax.y()));
        pointOut.setZ(closestPointOnAxis(pointIn.z(), cornerMin.z(), cornerMax.z()));
        return pointOut;
    }

    public static Point3i relativePositionTo(Point3i relPoint, ReadableTuple3i srcPoint) {
        return Point3i.immutableSubtract(relPoint, srcPoint);
    }

    /**
     * The relative position of the corner to another bounding box
     *
     * @param other the other box, against whom we consider our coordinates relatively
     * @return the difference between corners i.e. other bounding-box's corner - this bounding-box's
     *     corner
     */
    public Point3i relativePositionTo(BoundingBox other) {
        return relativePositionTo(cornerMin, other.cornerMin);
    }

    /**
     * A new bounding-box using relative position coordinates to another box
     *
     * @param other the other box, against whom we consider our coordinates relatively
     * @return a newly created bounding box with relative coordinates
     */
    public BoundingBox relativePositionToBox(BoundingBox other) {
        return new BoundingBox(relativePositionTo(other), extent);
    }

    /** For evaluating whether this bounding-box contains other points, boxes etc.? */
    public BoundingBoxContains contains() {
        return new BoundingBoxContains(this);
    }

    /** For evaluating the intersection between this bounding-box and others */
    public BoundingBoxIntersection intersection() {
        return new BoundingBoxIntersection(this);
    }

    /** For performing a union between this bounding-box and another */
    public BoundingBoxUnion union() {
        return new BoundingBoxUnion(this);
    }

    @Override
    public String toString() {
        return cornerMin.toString()
                + "+"
                + extent.toString()
                + "="
                + calculateCornerMaxExclusive().toString();
    }

    /**
     * Moves the bounding-box to the origin (0,0,0) but preserves the extent
     *
     * @return newly-created bounding box with shifted corner position (to the origin) and identical
     *     extent
     */
    public BoundingBox shiftToOrigin() {
        return new BoundingBox(extent);
    }

    /**
     * Shifts the bounding-box by adding to it i.e. adds a vector to the corner position
     *
     * @param shift what to add to the corner position
     * @return newly created bounding-box with shifted corner position and identical extent
     */
    public BoundingBox shiftBy(ReadableTuple3i shift) {
        return new BoundingBox(Point3i.immutableAdd(cornerMin, shift), extent);
    }

    /**
     * Shifts the bounding-box by subtracting from i.e. subtracts a vector from the corner position
     *
     * @param shift what to sutract from the corner position
     * @return newly created bounding-box with shifted corner position and identical extent
     */
    public BoundingBox shiftBackBy(ReadableTuple3i shift) {
        return new BoundingBox(Point3i.immutableSubtract(cornerMin, shift), extent);
    }

    /**
     * Assigns a new corner-location to the bounding-box
     *
     * @param cornerMinNew the new corner
     * @return a bounding-box with a new corner and the same extent
     */
    public BoundingBox shiftTo(Point3i cornerMinNew) {
        return new BoundingBox(cornerMinNew, extent);
    }

    /**
     * Assigns a new z-slice corner-location to the bounding-box
     *
     * @param crnrZNew the new value in Z for the corner
     * @return a bounding-box with a new z-slice corner and the same extent
     */
    public BoundingBox shiftToZ(int crnrZNew) {
        return new BoundingBox(cornerMin.duplicateChangeZ(crnrZNew), extent);
    }

    /**
     * Reflects the bounding box through the origin (i.e. {@code x, y, z} becomes {@code -x, -y,
     * -z})
     *
     * @return a bounding-box reflected through the origin
     */
    public BoundingBox reflectThroughOrigin() {
        return new BoundingBox(Point3i.immutableScale(cornerMin, -1), extent);
    }

    /**
     * Scales the bounding-box, both the corner-point and the extent
     *
     * @param scaleFactor scaling-factor
     * @return a new bounding-box with scaled corner-point and extent
     */
    public BoundingBox scale(ScaleFactor scaleFactor) {
        return scale(scaleFactor, scaledExtent(scaleFactor));
    }

    /**
     * Scales the bounding-box, both the corner-point and the extent - ensuring it remains inside a
     * containing-extent
     *
     * @param scaleFactor scaling-factor
     * @param clipTo clips scaled-object's bounding-box to ensure it always fit inside (to catch any
     *     rounding errors that push the bounding box outside the scene-boundary)
     * @return a new bounding-box with scaled corner-point and extent
     */
    public BoundingBox scaleClipTo(ScaleFactor scaleFactor, Extent clipTo) {
        Point3i cornerScaled = scaledCorner(scaleFactor);
        Extent extentScaled = scaledExtent(scaleFactor);
        BoundingBox boxScaled = new BoundingBox(cornerScaled, extentScaled);
        return boxScaled.clipTo(clipTo);
    }

    /**
     * Scales the bounding-box corner-point, and assigns a new extent
     *
     * @param scaleFactor scaling-factor
     * @param extentToAssign extent to assign
     * @return a new bounding-box with scaled corner-point and the specified extent
     */
    public BoundingBox scale(ScaleFactor scaleFactor, Extent extentToAssign) {
        return new BoundingBox(scaledCorner(scaleFactor), extentToAssign);
    }

    /** The bottom-left corner of the bounding box. */
    public ReadableTuple3i cornerMin() {
        /** Exposed via {@link ReadableTuple3i} to keep it read-only */
        return cornerMin;
    }

    private Extent scaledExtent(ScaleFactor scaleFactor) {
        return extent.scaleXYBy(scaleFactor);
    }

    private Point3i scaledCorner(ScaleFactor scaleFactor) {
        return ScaleFactorUtilities.scale(scaleFactor, cornerMin);
    }

    private void checkMaxMoreThanMin(ReadableTuple3i min, ReadableTuple3i max) {
        if ((max.x() < min.x()) || (max.y() < min.y()) || (max.z() < min.z())) {
            throw new AnchorFriendlyRuntimeException(
                    String.format(
                            "To create a bounding-box, the max-point %s must always be >= the min-point %s in all dimensions.",
                            max, min));
        }
    }

    private Point3d meanOfExtent(int subtractFromEachDimension) {
        return new Point3d(
                calculateMeanForDim(ReadableTuple3i::x, subtractFromEachDimension),
                calculateMeanForDim(ReadableTuple3i::y, subtractFromEachDimension),
                calculateMeanForDim(ReadableTuple3i::z, subtractFromEachDimension));
    }

    private double calculateMeanForDim(
            ToDoubleFunction<ReadableTuple3i> extractDim, int subtractFromEachDimension) {
        double midPointInExtent =
                (extractDim.applyAsDouble(extent.asTuple()) - subtractFromEachDimension) / 2;
        return extractDim.applyAsDouble(cornerMin) + midPointInExtent;
    }

    private static int closestPointOnAxis(double val, int axisMin, int axisMax) {

        if (val < axisMin) {
            return axisMin;
        }

        if (val > axisMax) {
            return axisMax;
        }

        return (int) val;
    }

    private static Point3i multiplyByTwo(Tuple3i point) {
        return Point3i.immutableScale(point, 2);
    }
}
