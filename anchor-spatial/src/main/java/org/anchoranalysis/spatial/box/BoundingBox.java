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

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.function.ToDoubleFunction;
import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.PointConverter;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.anchoranalysis.spatial.point.Tuple3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;
import org.anchoranalysis.spatial.scale.Scaler;

/**
 * A bounding-box in two or three dimensions.
 *
 * <p>A bounding-box in two dimensions should always set it's z-dimension's extent to {@code 1}.
 *
 * <p>This is an <i>immutable</i> class. No operation modifies the state of the existing object.
 */
@EqualsAndHashCode
@Accessors(fluent = true)
public final class BoundingBox implements Serializable, Comparable<BoundingBox> {

    private static final long serialVersionUID = 1L;

    /**
     * The bottom-left corner of the bounding box.
     *
     * <p>This the minimum point in all dimensions for the bounding-box.
     */
    private final Point3i cornerMin;
    
    /**
     * The top-right corner of the bounding box (inclusive).
     *
     * <p>This the maximum valid point in all dimensions for the bounding-box.
     * 
     * <p>It is calculated lazily when first needed by {@link #calculateCornerMaxInclusive()}.
     */
    private Point3i cornerMaxInclusive;
    
    /**
     * The top-right corner of the bounding box (exclusive).
     *
     * <p>This is {@code cornerMaxInclusive} with 1 added in each dimension.
     * 
     * <p>It is calculated lazily when first needed by {@link #calculateCornerMaxExclusive()}.
     */
    private Point3i cornerMaxExclusive;

    /**
     * Dimensions in pixels needed to represent the bounding box.
     *
     * <p>This emanates in a positive direction from {@code cornerMin} to define the complete box.
     */
    @Getter private final Extent extent;

    /**
     * Constructs a bounding-box to cover the entirety of a certain extent.
     *
     * @param extent the extent.
     */
    public BoundingBox(Extent extent) {
        this(new Point3i(), extent);
    }

    /**
     * Creates from two {@code double} points (a minimum corner and a maximum corner).
     *
     * @param cornerMinInclusive minimum point in each dimension of the bounding-box (that exists
     *     inside the box).
     * @param cornerMaxInclusive maximum point in each dimension of the bounding-box (that exists
     *     inside the box).
     */
    public BoundingBox(Point3d cornerMinInclusive, Point3d cornerMaxInclusive) {
        this(
                PointConverter.intFromDoubleFloor(cornerMinInclusive),
                PointConverter.intFromDoubleCeil(cornerMaxInclusive));
    }

    /**
     * Creates from two {@code int} points (a minimum corner and a maximum corner).
     *
     * @param cornerMinInclusive minimum point in each dimension of the bounding-box (that exists
     *     inside the box).
     * @param cornerMaxInclusive maximum point in each dimension of the bounding-box (that exists
     *     inside the box).
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

    /**
     * Creates a bounding-box from a corner and an extent.
     * 
     * TODO create two constructors for when to reuse it and when not to.
     *
     * @param cornerMin the corner that is the minimum point in all dimensions for the bounding-box.
     * @param extent the size of the bounding-box eminating from {@code cornerMin}.
     */
    public BoundingBox(ReadableTuple3i cornerMin, Extent extent) {
        // Note this always duplicates the corner, creating some needless object-creation
        this.cornerMin = new Point3i(cornerMin);
        this.extent = extent;
    }
    
    /**
     * Creates a bounding-box from a corner and an extent - reusing {@code cornerMin} internally.
     * 
     * <p>The {@code cornerMin} is <b>not</b> duplicated before being stored internally. It should not be subsequently modified externally.
     * 
     * <p>{code cornerMin} will also never be changed internally, so it is safe to pass a constant using this method.
     * 
     * See {@link #createDuplicate(ReadableTuple3i, Extent)} for an alternative that duplicates {@code cornerMin}.
     *
     * @param cornerMin the corner that is the minimum point in all dimensions for the bounding-box.
     * @param extent the size of the bounding-box emanating from {@code cornerMin}.
     * @return the newly created {@link BoundingBox}.
     */
    public static BoundingBox createReuse(ReadableTuple3i cornerMin, Extent extent) {
        // Note this always duplicates the corner, creating some needless object-creation
    	return new BoundingBox(cornerMin, extent);
    }
    
    /**
     * Creates a bounding-box from a corner and an extent - duplicating {@code cornerMin}.
     * 
     * <p>The {@code cornerMin} is duplicated before being stored internally. This makes it safe to further modify it externally.
     *
     * See {@link #createReuse(ReadableTuple3i, Extent)} for an alternative that does not duplicate {@code cornerMin}.
     * 
     * @param cornerMin the corner that is the minimum point in all dimensions for the bounding-box.
     * @param extent the size of the bounding-box emanating from {@code cornerMin}.
     * @return the newly created {@link BoundingBox}.
     */
    public static BoundingBox createDuplicate(ReadableTuple3i cornerMin, Extent extent) {
        // Note this always duplicates the corner, creating some needless object-creation
    	return new BoundingBox(new Point3i(cornerMin), extent);
    }

    /**
     * A mid-point in the bounding box, corresponding to the exact half-way point between {@code
     * (corner+extent)/2}.
     *
     * <p>It may not be integral, possibly ending with {@code 0.5}
     *
     * @return a newly created point representing the mid-point.
     */
    public Point3d midpoint() {
        return meanOfExtent(0);
    }

    /**
     * A mid-point in the bounding-box, corresponding to the mean of all points inside the box.
     *
     * <p>i.e. in each dimension, it is {@code (corner+extent-1)/2}
     *
     * <p>It is guaranteed to be integral, by flooring.
     *
     * <p>This is similar to {@link #midpoint} but can be marginally shifted left.
     *
     * @return the center-of-gravity.
     */
    public Point3i centerOfGravity() {
        return PointConverter.intFromDoubleFloor(meanOfExtent(1));
    }

    /**
     * Collapses the z-dimension of the box to a single voxel depth, and a corner at {@code 0}
     * voxels.
     *
     * @return a newly created {@code BoundingBox} with identical X and Y values, but with the Z
     *     dimension flattened.
     */
    public BoundingBox flattenZ() {
        return BoundingBox.createReuse(cornerMin.duplicateChangeZ(0), extent.duplicateChangeZ(1));
    }

    /**
     * Creates a copied {@link BoundingBox} but with a different extent.
     *
     * @param extent the extent to assign.
     * @return a newly-created {@link BoundingBox} that has a changed extent, but is otherwise
     *     identical.
     */
    public BoundingBox changeExtent(Extent extent) {
        return BoundingBox.createReuse(cornerMin, extent);
    }

    /**
     * Creates a copied {@link BoundingBox} but with a different extent.
     *
     * @param extentOperator an operator that changes the extent.
     * @return a newly-created {@link BoundingBox} that has a changed extent, but is otherwise
     *     identical.
     */
    public BoundingBox changeExtent(UnaryOperator<Extent> extentOperator) {
        return changeExtent(extentOperator.apply(extent));
    }

    /**
     * Creates a copied {@link BoundingBox} but with a different corner and extent in the
     * Z-dimension.
     *
     * @param cornerZ the corner to assign in the z-dimension.
     * @param extentZ the extent to assign in the z-dimension.
     * @return a newly-created {@link BoundingBox} that has a changed extent and corner in the
     *     Z-dimension, but is otherwise identical.
     */
    public BoundingBox changeZ(int cornerZ, int extentZ) {
        return BoundingBox.createReuse(
                cornerMin.duplicateChangeZ(cornerZ), extent.duplicateChangeZ(extentZ));
    }

    /**
     * Creates a copied {@link BoundingBox} but with a different extent in the Z-dimension.
     *
     * @param extentZ the extent to assign in the z-dimension.
     * @return a newly-created {@link BoundingBox} that has a changed extent in the Z-dimension, but
     *     is otherwise identical.
     */
    public BoundingBox changeExtentZ(int extentZ) {
        return BoundingBox.createReuse(cornerMin, extent.duplicateChangeZ(extentZ));
    }

    /**
     * Does the bounding-box have an edge at the border of an image of size {@code extent}?
     *
     * <p>The border of the image is defined as the exterior including all voxels that are a minimum
     * or maximum in any given dimension.
     *
     * @param extent the size of the image.
     * @return true iff the bounding box lies at the border.
     */
    public boolean atBorder(Extent extent) {

        if (atBorderXY(extent)) {
            return true;
        }

        return atBorderZ(extent);
    }

    /**
     * Like {@link #atBorder(Extent)} but considers only the X- and Y- dimensions.
     *
     * @param extent the size of the image.
     * @return true iff the bounding box lies at the border along the X-axis or Y-axis.
     */
    public boolean atBorderXY(Extent extent) {

        ReadableTuple3i cornerMax = this.calculateCornerMaxExclusive();

        if (cornerMin.x() == 0) {
            return true;
        }
        if (cornerMin.y() == 0) {
            return true;
        }

        if (cornerMax.x() == extent.x()) {
            return true;
        } else {
            return cornerMax.y() == extent.y();
        }
    }

    /**
     * Like {@link #atBorder(Extent)} but considers only the Z-dimension.
     *
     * @param extent the size of the image.
     * @return true iff the bounding box lies at the border along the Z-axis.
     */
    public boolean atBorderZ(Extent extent) {

        ReadableTuple3i cornerMax = this.calculateCornerMaxExclusive();

        if (cornerMin.z() == 0) {
            return true;
        } else {
            return cornerMax.z() == extent.z();
        }
    }

    /**
     * Grow the bounding-box by {@code toAdd} amount in each dimension in both positive and negative
     * directions.
     *
     * <p>The box will never be allowed grow larger than {@code containingExtent}.
     *
     * <p>Unless constrained by the above, the bounding-box's corner and extent will typically both
     * change.
     *
     * <p>This is an <i>immutable</i> operation and current state will not be affected.
     *
     * @param toAdd the number of voxels to grow by in each direction. Each component should be
     *     non-negative.
     * @param containingExtent an extent the box must never grow beyond, in either the positive or
     *     negative directions.
     * @return a newly created grown {@code BoundingBox} as per the above.
     */
    public BoundingBox growBy(Tuple3i toAdd, Extent containingExtent) {

        Preconditions.checkArgument(toAdd.x() >= 0);
        Preconditions.checkArgument(toAdd.y() >= 0);
        Preconditions.checkArgument(toAdd.z() >= 0);

        // Subtract the padding from the corner
        Point3i cornerMinShifted = Point3i.immutableSubtract(cornerMin, toAdd);

        // Add double-padding in each dimension to the extent
        Extent extentGrown = extent.growBy(multiplyByTwo(toAdd));

        // Clamp to make sure we remain within bounds
        return BoundingBox.createReuse(cornerMinShifted, extentGrown).clampTo(containingExtent);
    }

    /**
     * The minimum corner of the bounding box in each dimension.
     *
     * @return the point used internally as a corner (exposed read-only).
     */
    public ReadableTuple3i cornerMin() {
        /** Exposed via {@link ReadableTuple3i} to keep it read-only */
        return cornerMin;
    }
    
    /**
     * The maximum (right-most) point <i>inside</i> the box.
     *
     * <p>This means that iterators should be {@code <= calculateCornerMax()}.
     *
     * @return a newly created {@link Point3i}, indicating the maximum point inside the box in each
     *     dimension.
     */
    public ReadableTuple3i calculateCornerMaxInclusive() {
    	if (cornerMaxInclusive==null) {
	        cornerMaxInclusive = new Point3i();
	        cornerMaxInclusive.setX(cornerMin.x() + extent.x() - 1);
	        cornerMaxInclusive.setY(cornerMin.y() + extent.y() - 1);
	        cornerMaxInclusive.setZ(cornerMin.z() + extent.z() - 1);
    	}
    	return cornerMaxInclusive;
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
    public ReadableTuple3i calculateCornerMaxExclusive() {
    	if (cornerMaxExclusive==null) {
    		cornerMaxExclusive = new Point3i();
    		cornerMaxExclusive.setX(cornerMin.x() + extent.x());
    		cornerMaxExclusive.setY(cornerMin.y() + extent.y());
    		cornerMaxExclusive.setZ(cornerMin.z() + extent.z());
    	}
    	return cornerMaxExclusive;
    }

    /**
     * Ensures that the box fits inside a {@link Extent} by reducing any values to their limits in
     * the respective dimension.
     *
     * <p>Values that are negative are forced to be 0.
     *
     * <p>Values that are larger than the corresponding dimension in {@code Extent} are reduced to
     * the maximum permitted in that dimension.
     *
     * @param extent the extent the box is made fit inside.
     * @return a newly created bounding-box, if any changes are needed. Otherwise the existing object is reused.
     */
    public BoundingBox clampTo(Extent extent) {

        if (cornerMin.x() >= extent.x()
                || cornerMin.y() >= extent.y()
                || cornerMin.z() >= extent.z()) {
            throw new AnchorFriendlyRuntimeException(
                    String.format(
                            "Corner-min (%s) is outside the clamping region (%s)",
                            cornerMin, extent));
        }
        
        ReadableTuple3i cornerMax = calculateCornerMaxInclusive();
        boolean cornerMinValid = ClampToUtilities.pointNonZero(cornerMin); 
        boolean cornerMaxValid = ClampToUtilities.pointLessThan(cornerMax, extent);
        if (cornerMinValid && cornerMaxValid) {
        	return this;
        } else {
        	ReadableTuple3i min = cornerMinValid ? cornerMin : ClampToUtilities.replaceNegativeWithZero(cornerMin);
	        ReadableTuple3i max = cornerMaxValid ? cornerMax : ClampToUtilities.limitToExtent(cornerMax, extent);
		    return new BoundingBox(min, max);
        }
    }

    /**
     * The relative position of the corner to another bounding box.
     *
     * @param other the other box, against whom we consider our coordinates relatively.
     * @return the difference between corners i.e. {@code other bounding-box's corner - this
     *     bounding-box's corner}.
     */
    public Point3i relativePositionTo(BoundingBox other) {
        return Point3i.immutableSubtract(cornerMin, other.cornerMin);
    }

    /**
     * A new bounding-box using relative position coordinates to another box.
     *
     * @param other the other box, against whom we consider our coordinates relatively.
     * @return a newly created bounding box with relative coordinates.
     */
    public BoundingBox relativePositionToBox(BoundingBox other) {
        return BoundingBox.createReuse(relativePositionTo(other), extent);
    }

    /**
     * For evaluating whether this bounding-box contains other points, boxes etc.?
     *
     * @return a newly-created class to evaluate the <i>contains</i> relationship.
     */
    public BoundingBoxContains contains() {
        return new BoundingBoxContains(this);
    }

    /**
     * For evaluating the intersection between this bounding-box and others.
     *
     * @return a newly-created class to evaluate the <i>intersection</i> relationship.
     */
    public BoundingBoxIntersection intersection() {
        return new BoundingBoxIntersection(this);
    }

    /**
     * For performing a union between this bounding-box and another.
     *
     * @return a newly-created class to evaluate the <i>union</i> relationship.
     */
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
     * Moves the bounding-box to the origin (0,0,0) but preserves the extent.
     *
     * @return newly-created bounding box with shifted corner position (to the origin) and identical
     *     extent.
     */
    public BoundingBox shiftToOrigin() {
        return new BoundingBox(extent);
    }

    /**
     * Shifts the bounding-box forwards.
     *
     * <p>i.e. adds a vector to the corner position.
     *
     * @param shift what to add to the corner position.
     * @return newly created bounding-box with shifted corner position and identical extent.
     */
    public BoundingBox shiftBy(ReadableTuple3i shift) {
        return BoundingBox.createReuse(Point3i.immutableAdd(cornerMin, shift), extent);
    }

    /**
     * Shifts the bounding-box backwards.
     *
     * <p>i.e. subtracts a vector from the corner position.
     *
     * @param shift what to subtract from the corner position.
     * @return newly created bounding-box with shifted corner position and identical extent.
     */
    public BoundingBox shiftBackBy(ReadableTuple3i shift) {
        return BoundingBox.createReuse(Point3i.immutableSubtract(cornerMin, shift), extent);
    }

    /**
     * Assigns a new corner-location to the bounding-box.
     *
     * @param cornerMinToAssign the new corner.
     * @return a bounding-box with a new corner and the same extent.
     */
    public BoundingBox shiftTo(Point3i cornerMinToAssign) {
        return new BoundingBox(cornerMinToAssign, extent);
    }

    /**
     * Assigns a new z-slice corner-location to the bounding-box.
     *
     * @param cornerZToAssign the new value in Z for the corner.
     * @return a newly-created bounding-box with a new z-slice corner and the same extent.
     */
    public BoundingBox shiftToZ(int cornerZToAssign) {
        return BoundingBox.createReuse(cornerMin.duplicateChangeZ(cornerZToAssign), extent);
    }

    /**
     * Reflects the bounding box through the origin.
     *
     * <p>i.e. {@code x, y, z} becomes {@code -x, -y, -z}.
     *
     * @return a newly-created bounding-box reflected through the origin.
     */
    public BoundingBox reflectThroughOrigin() {
        return BoundingBox.createReuse(Point3i.immutableScale(cornerMin, -1), extent);
    }

    /**
     * Scales the bounding-box, both the corner-point and the extent.
     *
     * @param scaleFactor the scaling-factor.
     * @return a new bounding-box with scaled corner-point and extent.
     */
    public BoundingBox scale(ScaleFactor scaleFactor) {
        return scale(scaleFactor, scaledExtent(scaleFactor));
    }

    /**
     * Scales the bounding-box, both the corner-point and the extent - ensuring it remains inside a
     * containing-extent.
     *
     * @param scaleFactor scaling-factor.
     * @param clampTo clamps scaled-object's bounding-box to ensure it always fit inside (to catch
     *     any rounding errors that push the bounding box outside the scene-boundary).
     * @return a new bounding-box with scaled corner-point and extent.
     */
    public BoundingBox scaleClampTo(ScaleFactor scaleFactor, Extent clampTo) {
        Point3i cornerScaled = scaledCorner(scaleFactor);
        Extent extentScaled = scaledExtent(scaleFactor);
        BoundingBox boxScaled = BoundingBox.createReuse(cornerScaled, extentScaled);
        return boxScaled.clampTo(clampTo);
    }

    /**
     * Scales the bounding-box corner-point, and assigns a new extent.
     *
     * @param scaleFactor scaling-factor.
     * @param extentToAssign extent to assign.
     * @return a new bounding-box with scaled corner-point and the specified extent.
     */
    public BoundingBox scale(ScaleFactor scaleFactor, Extent extentToAssign) {
        return BoundingBox.createReuse(scaledCorner(scaleFactor), extentToAssign);
    }

    @Override
    public int compareTo(BoundingBox other) {
        int compareCornerMin = cornerMin.compareTo(other.cornerMin);
        if (compareCornerMin != 0) {
            return compareCornerMin;
        }

        int compareExtent = extent.compareTo(other.extent);
        if (compareExtent != 0) {
            return compareExtent;
        }

        return 0;
    }

    private Extent scaledExtent(ScaleFactor scaleFactor) {
        return extent.scaleXYBy(scaleFactor, true);
    }

    private Point3i scaledCorner(ScaleFactor scaleFactor) {
        return Scaler.scale(scaleFactor, cornerMin);
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

    private static Point3i multiplyByTwo(Tuple3i point) {
        return Point3i.immutableScale(point, 2);
    }
}
