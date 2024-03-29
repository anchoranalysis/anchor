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

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.functional.checked.CheckedIntConsumer;
import org.anchoranalysis.spatial.axis.Axis;
import org.anchoranalysis.spatial.point.Point2i;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.anchoranalysis.spatial.point.consumer.OffsettedPointTwoDimensionalConsumer;
import org.anchoranalysis.spatial.point.consumer.OffsettedScalarTwoDimensionalConsumer;
import org.anchoranalysis.spatial.point.consumer.PointTwoDimensionalConsumer;
import org.anchoranalysis.spatial.scale.ScaleFactor;
import org.anchoranalysis.spatial.scale.Scaler;

/**
 * The sizes: width, height, depth etc. of an entity in 2 or 3 dimensions.
 *
 * <p>When describing a 2D entity, the depth (Z-axis value) is always {@code 1}.
 *
 * <p>Each size corresponds to the perpendicular Cartesian axes i.e. X, Y and Z axes.
 *
 * <p>This class is <b>immutable</b>. No operation will modify existing state.
 *
 * <p>The area (width by height) is pre-calculated to make certain operations like calculating
 * offsets (which may need to be called frequently) efficient. However, for sizes producing very
 * large areas that exceed the bounds of an {@code int} these need to be handled separately and
 * carefully. These cases can be checked for by calling {@link #areaXY()}.
 */
@Accessors(fluent = true)
public final class Extent implements Serializable, Comparable<Extent> {

    private static final long serialVersionUID = 1L;

    /** Sizes in each dimension. */
    private final ReadableTuple3i size;

    /**
     * Size in X multiplied by size in Y.
     *
     * <p>This may be convenient for calculating offsets and for iterations.
     *
     * <p>Note if the area is too large to be stored as an int, a -1 is stored instead. This should
     * be checked for, with an appropriate error message, or alternative behavior occurring in these
     * instances.
     *
     * <p>We avoid storing the area as a long, as it would create computational overhead in the vast
     * bulk of cases where an int is sufficient, especially for operations like calculating offsets
     * that may occur repeatedly in a loop. It is preferable to handle the outlier cases of very
     * large areas separately.
     */
    private final int areaXY;

    /**
     * Creates with with only X and Y dimensions.
     *
     * <p>The z-dimension is assigned a value of {@code 1}.
     *
     * @param x size along the X-axis dimension.
     * @param y size along the Y-axis dimension.
     */
    public Extent(int x, int y) {
        this(x, y, 1);
    }

    /**
     * Creates with X and Y and Z dimensions.
     *
     * @param x size along the X-axis dimension.
     * @param y size along the Y-axis dimension.
     * @param z size along the Z-axis dimension.
     */
    public Extent(int x, int y, int z) {
        this(new Point3i(x, y, z));
    }

    /**
     * Creates from a {@link ReadableTuple3i} representing the sizes in each dimension, where the
     * tuple <b>is not used</b> internally.
     *
     * <p>The tuple is not used internally, with its values being copied in the constructor.
     *
     * @param tuple a tuple with the extent size's for each dimension.
     * @return a newly created extent - that doesn't reuse {@code tuple} internally.
     */
    public static Extent createFromTupleDuplicate(ReadableTuple3i tuple) {
        return new Extent(new Point3i(tuple));
    }

    /**
     * Creates from a {@link ReadableTuple3i} representing the sizes in each dimension, where the
     * tuple <b>is used</b> internally.
     *
     * @param tuple a tuple with the extent size's for each dimension.
     * @return a newly created extent - that reuses {@code tuple} internally.
     */
    public static Extent createFromTupleReuse(ReadableTuple3i tuple) {
        return new Extent(new Point3i(tuple));
    }

    /**
     * Creates from a tuple that is reused internally.
     *
     * @param size a tuple with the extent size's for each dimension.
     */
    private Extent(ReadableTuple3i size) {
        this.size = size;

        this.areaXY = calculateAreaXY();

        if (size.x() == 0 || size.y() == 0 || size.z() == 0) {
            throw new AnchorFriendlyRuntimeException(
                    "An extent must have at least one voxel in every dimension.");
        }

        if (size.x() < 0 || size.y() < 0 || size.z() < 0) {
            throw new AnchorFriendlyRuntimeException(
                    "An extent may not be negative in any dimension");
        }
    }

    /**
     * Size in X multiplied by size in Y.
     *
     * <p>This may be convenient for calculating offsets and for iterations.
     *
     * <p>Note that for very large sizes (e.g. the sizes of whole-slide images) an int may be
     * insufficiently large to capture the area. Consider instead using {@link
     * #calculateAreaXYAsDouble()} in these cases.
     *
     * @return the X-size multiplied by the Y-size.
     * @throws AnchorFriendlyRuntimeException if the area is too large to be expressed as an {@code
     *     int}.
     */
    public int areaXY() {
        return areaXYAsInt();
    }

    /**
     * Calculates the area freshly by multiplying the x-size with y-size, as doubles.
     *
     * <p>This is useful if the area would otherwise be too large to be represented as an int.
     *
     * <p>Any previously calculated area is ignored.
     *
     * @return the area calculated as a double.
     */
    public double calculateAreaXYAsDouble() {
        return ((double) size.x()) * size.y();
    }

    /**
     * Calculates the volume of the {@link Extent} when considered as a box.
     *
     * <p>This is is the size in the X, Y and Z dimensions multiplied together.
     *
     * @return the volume in voxels.
     */
    public long calculateVolume() {
        if (areaXY >= 0) {
            return ((long) areaXY) * size.z();
        } else {
            return (((long) size.x()) * size.y()) * size.z();
        }
    }

    /**
     * Like {@link #calculateVolume()} but uses an {@code int} to calculate the volume.
     *
     * <p>A {@link AnchorFriendlyRuntimeException} is thrown if an overflow occurs.
     *
     * @return the volume in voxels.
     */
    public int calculateVolumeAsInt() {
        long volume = calculateVolume();
        if (volume > Integer.MAX_VALUE) {
            throw new AnchorFriendlyRuntimeException(
                    "The volume cannot be expressed as an int, as it is higher than the maximum bound");
        }
        return (int) volume;
    }

    /**
     * Does the extent contain zero voxels?
     *
     * @return true if any dimension has size 0.
     */
    public boolean isEmpty() {
        return (areaXY == 0) || (size.z() == 0);
    }

    /**
     * The size in the X dimension.
     *
     * @return the size.
     */
    public int x() {
        return size.x();
    }

    /**
     * The size in the Y dimension.
     *
     * @return the size.
     */
    public int y() {
        return size.y();
    }

    /**
     * The size in the Z dimension.
     *
     * @return the size.
     */
    public int z() {
        return size.z();
    }

    /**
     * The size in the dimension identified by {@code dimensionIndex}.
     *
     * @param dimensionIndex the dimension to return a size for, as per {@code
     *     ReadableTuple3i#valueByDimension(int)}.
     * @return the size.
     */
    public int valueByDimension(int dimensionIndex) {
        return size.valueByDimension(dimensionIndex);
    }

    /**
     * The size in the dimension identified by {@code axis}.
     *
     * @param axis the dimension to return a size for, as per {@code
     *     ReadableTuple3i#valueByDimension(Axis)}.
     * @return the size.
     */
    public int valueByDimension(Axis axis) {
        return size.valueByDimension(axis);
    }

    /**
     * Exposes the extent as a tuple.
     *
     * <p><b>Importantly,</b> this class is designed to be <b>immutable</b>, so this tuple should be
     * treated as read-only, and never modified.
     *
     * @return the extent's width, height, depth as a tuple.
     */
    public ReadableTuple3i asTuple() {
        return size;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + size.x();
        result = prime * result + size.y();
        result = prime * result + size.z();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Extent other = (Extent) obj;
        return size.equals(other.size);
    }

    /**
     * Checks for equality with another extent ignoring any differences in the Z dimension.
     *
     * @param other the extent to check for equality with.
     * @return true if the current extent is identical to {@code obj} ignoring the Z dimension.
     */
    public boolean equalsIgnoreZ(Extent other) {
        return (size.x() == other.x()) && (size.y() == other.y());
    }

    @Override
    public String toString() {
        return String.format("[%d,%d,%d]", x(), y(), z());
    }

    /**
     * Calculates a XY-offset of a point in a buffer whose dimensions are this extent.
     *
     * @param x the value in the X-dimension for the point.
     * @param y the value in the Y-dimension for the point.
     * @return the offset, pertaining only to all dimensions.
     */
    public final int offset(int x, int y) {
        return (y * size.x()) + x;
    }

    /**
     * Calculates a XYZ-offset of a point in a buffer whose dimensions are this extent.
     *
     * <p>To be computationally efficient, <b>this does not actively check that {@code areaXY} is
     * non-negative</b>. It is the responsibility of the caller of this function to otherwise check
     * this, or infer it from context.
     *
     * @param x the value in the X-dimension for the point.
     * @param y the value in the Y-dimension for the point.
     * @param z the value in the Z-dimension for the point.
     * @return the offset, pertaining only to all dimensions.
     */
    public final int offset(int x, int y, int z) {
        return (z * areaXY) + (y * x()) + x;
    }

    /**
     * Calculates a XYZ-offset of a point in a buffer whose dimensions are this extent.
     *
     * <p>To be computationally efficient, <b>this does not actively check that {@code areaXY} is
     * non-negative</b>. It is the responsibility of the caller of this function to otherwise check
     * this, or infer it from context.
     *
     * @param point the point to calculate an offset for.
     * @return the offset, pertaining only to all dimensions.
     */
    public final int offset(ReadableTuple3i point) {
        return offset(point.x(), point.y(), point.z());
    }

    /**
     * Calculates a XY-offset of a point in a buffer whose dimensions are this extent.
     *
     * <p>To be computationally efficient, <b>this does not actively check that {@code areaXY} is
     * non-negative</b>. It is the responsibility of the caller of this function to otherwise check
     * this, or infer it from context.
     *
     * @param point the point to calculate an offset for.
     * @return the offset, pertaining only to the X and Y dimensions.
     */
    public final int offset(Point2i point) {
        return offset(point.x(), point.y(), 0);
    }

    /**
     * Calculates a XY-offset of a point in a buffer whose dimensions are this extent.
     *
     * <p>To be computationally efficient, <b>this does not actively check that {@code areaXY} is
     * non-negative</b>. It is the responsibility of the caller of this function to otherwise check
     * this, or infer it from context.
     *
     * @param point the point to calculate an offset for.
     * @return the offset, pertaining only to the X and Y dimensions.
     */
    public final int offsetSlice(ReadableTuple3i point) {
        return offset(point.x(), point.y(), 0);
    }

    /**
     * Creates a copy of the current {@link Extent} with the value for the X-dimension changed.
     *
     * @param xToAssign the value to assign for the x-dimension.
     * @return the copy, with a changed x-value.
     */
    public Extent duplicateChangeX(int xToAssign) {
        return new Extent(xToAssign, size.y(), size.z());
    }

    /**
     * Creates a copy of the current {@link Extent} with the value for the Y-dimension changed.
     *
     * @param yToAssign the value to assign for the y-dimension.
     * @return the copy, with a changed y-value.
     */
    public Extent duplicateChangeY(int yToAssign) {
        return new Extent(size.x(), yToAssign, size.z());
    }

    /**
     * Creates a copy of the current {@link Extent} with the value for the Z-dimension changed.
     *
     * @param zToAssign the value to assign for the z-dimension.
     * @return the copy, with a changed z-value.
     */
    public Extent duplicateChangeZ(int zToAssign) {
        return new Extent(size.x(), size.y(), zToAssign);
    }

    /**
     * Is a value contained within the extent on the X-axis?
     *
     * @param value the value to check.
     * @return true iff the value is non-negative and less than the size of the extent in the
     *     X-axis.
     */
    public boolean containsX(double value) {
        return value >= 0 && value < x();
    }

    /**
     * Is a value contained within the extent on the Y-axis?
     *
     * @param value the value to check.
     * @return true iff the value is non-negative and less than the size of the extent in the
     *     Y-axis.
     */
    public boolean containsY(double value) {
        return value >= 0 && value < y();
    }

    /**
     * Is a value contained within the extent on the Z-axis?
     *
     * @param value the value to check.
     * @return true iff the value is non-negative and less than the size of the extent in the
     *     Z-axis.
     */
    public boolean containsZ(double value) {
        return value >= 0 && value < z();
    }

    /**
     * Is a value contained within the extent on the X-axis?
     *
     * @param value the value to check.
     * @return true iff the value is non-negative and less than the size of the extent in the
     *     X-axis.
     */
    public boolean containsX(int value) {
        return value >= 0 && value < x();
    }

    /**
     * Is a value contained within the extent on the Y-axis?
     *
     * @param value the value to check.
     * @return true iff the value is non-negative and less than the size of the extent in the
     *     Y-axis.
     */
    public boolean containsY(int value) {
        return value >= 0 && value < y();
    }

    /**
     * Is a value contained within the extent on the Z-axis?
     *
     * @param value the value to check.
     * @return true iff the value is non-negative and less than the size of the extent in the
     *     Z-axis.
     */
    public boolean containsZ(int value) {
        return value >= 0 && value < z();
    }

    /**
     * Is a point of type {@link Point2i} contained within the extent in the XY plane?
     *
     * <p>The z-dimension is ignored.
     *
     * @param point the point to check.
     * @return true iff the point exists within the plane.
     */
    public boolean contains(Point2i point) {
        return containsX(point.x()) && containsY(point.y());
    }

    /**
     * Is a point of type {@link Point3d} contained within the extent?
     *
     * @param point the point to check.
     * @return true iff the point exists within the extent, considering all axes.
     */
    public boolean contains(Point3d point) {
        return containsX(point.x()) && containsY(point.y()) && containsZ(point.z());
    }

    /**
     * Is a point of type {@link ReadableTuple3i} contained within the extent?
     *
     * @param point the point to check.
     * @return true iff the point exists within the extent, considering all axes.
     */
    public boolean contains(ReadableTuple3i point) {
        return containsX(point.x()) && containsY(point.y()) && containsZ(point.z());
    }

    /**
     * Is a point contained within the extent?
     *
     * @param x the value of the point on the x-axis.
     * @param y the value of the point on the y-axis.
     * @param z the value of the point on the z-axis.
     * @return true iff the point exists within the extent, considering all axes.
     */
    public boolean contains(int x, int y, int z) {

        if (x < 0) {
            return false;
        }

        if (y < 0) {
            return false;
        }

        if (z < 0) {
            return false;
        }

        if (x >= size.x()) {
            return false;
        }

        if (y >= size.y()) {
            return false;
        }

        return (z < size.z());
    }

    /**
     * Is {@code box} entirely contained within the extent?
     *
     * @param box the bounding-box to check.
     * @return true iff {@code} only describes space contained in the current extent.
     */
    public boolean contains(BoundingBox box) {
        return contains(box.cornerMin()) && contains(box.calculateCornerMaxInclusive());
    }

    /**
     * Scales the X- and Y- dimensions by a scaling-factor.
     *
     * @param scaleFactor the scaling-factor to multiply the respective X and Y dimension values by.
     * @param round if true, each dimension is rounded to the nearest whole number. If false, it is
     *     <i>ceil</i>ed upwards to the nearest number.
     * @return a new {@link Extent} whose X and Y values are scaled versions of the current values,
     *     and Z value is unchanged.
     */
    public Extent scaleXYBy(ScaleFactor scaleFactor, boolean round) {
        return new Extent(
                immutablePointOperation(
                        point -> {
                            point.setX(Scaler.scaleMultiplex(scaleFactor.x(), x(), round));
                            point.setY(Scaler.scaleMultiplex(scaleFactor.y(), y(), round));
                        }));
    }

    /**
     * Scales <i>all</i> dimensions by a scaling-factor.
     *
     * @param scaleFactor the scaling-factor to multiply the respective dimension values by.
     * @param round if true, each dimension is rounded to the nearest whole number. If false, it is
     *     <i>ceil</i>ed upwards to the nearest number.
     * @return a new {@link Extent} whose dimension values are scaled versions of the current
     *     values, with a minimum of 1.
     */
    public Extent scaleXYBy(double scaleFactor, boolean round) {
        return new Extent(
                immutablePointOperation(
                        point -> {
                            point.setX(Scaler.scaleMultiplex(scaleFactor, x(), round));
                            point.setY(Scaler.scaleMultiplex(scaleFactor, y(), round));
                        }));
    }

    /**
     * Creates a new {@link Extent} with each dimension decreased by one.
     *
     * @return the new extent.
     */
    public Point3i createMinusOne() {
        return immutablePointOperation(p -> p.subtract(1));
    }

    /**
     * Creates a new {@link Extent} with {@code toAdd} size <b>added to</b> each dimension.
     *
     * @param toAdd the number of voxels to add to all dimensions.
     * @return a newly created {@link Extent} grown as per above.
     */
    public Extent growBy(int toAdd) {
        return growBy(new Point3i(toAdd));
    }

    /**
     * Creates a new {@link Extent} with {@code toAdd} size <b>added to</b> each respective
     * dimension.
     *
     * @param toAdd the number of voxels to add to each dimension.
     * @return a newly created {@link Extent} grown as per above.
     */
    public Extent growBy(ReadableTuple3i toAdd) {
        return new Extent(Point3i.immutableAdd(size, toAdd));
    }

    /**
     * Creates a new {@link Extent} with {@code toSubtract} size <b>subtracted from</b> each
     * respective dimension.
     *
     * @param toSubtract the number of voxels to subtract from each dimension.
     * @return a newly created {@link Extent} shrunk as per above.
     */
    public Extent shrinkBy(ReadableTuple3i toSubtract) {
        Point3i sizeToAssign = Point3i.immutableSubtract(size, toSubtract);
        if (sizeToAssign.x() >= 1 && sizeToAssign.y() >= 1 && sizeToAssign.z() >= 1) {
            return new Extent(sizeToAssign);
        } else {
            throw new AnchorFriendlyRuntimeException(
                    String.format("Cannot shrink by %s as it is larger than %s", toSubtract, size));
        }
    }

    /**
     * Intersects this extent with another (i.e. takes the smaller value in each dimension).
     *
     * @param other the other.
     * @return a newly-created extent that is the intersection of this and another.
     */
    public Extent intersectWith(Extent other) {
        return new Extent(Point3i.elementwiseOperation(size, other.size, Math::min));
    }

    /**
     * Collapses the Z dimension.
     *
     * @return a new otherwise identical extent bit with size of of 1 in Z-dimension.
     */
    public Extent flattenZ() {
        return new Extent(new Point3i(size.x(), size.y(), 1));
    }

    /**
     * Returns true if any dimension in this extent is larger than the corresponding dimension in
     * {@code other} extent.
     *
     * @param other extent to compare to.
     * @return true or false (if all dimensions or less than or equal to their corresponding
     *     dimension in {@code other}).
     */
    public boolean anyDimensionIsLargerThan(Extent other) {
        if (x() > other.x()) {
            return true;
        }
        if (y() > other.y()) {
            return true;
        }
        return z() > other.z();
    }

    /**
     * Calls processor once for each x and y-values in the range.
     *
     * <p>This occurs in ascending order (x-dimension increments first, y-dimension increments
     * second).
     *
     * @param <E> a checked-exception that {@code indexConsumer} may throw.
     * @param pointConsumer called for each point.
     * @throws E if {@code indexConsumer} throws this exception.
     */
    public <E extends Exception> void iterateOverXY(
            OffsettedScalarTwoDimensionalConsumer<E> pointConsumer) throws E {
        int offset = 0;
        for (int y = 0; y < size.y(); y++) {
            for (int x = 0; x < size.x(); x++) {
                pointConsumer.accept(x, y, offset++);
            }
        }
    }

    /**
     * Calls processor once for each x and y-values in the range.
     *
     * <p>This occurs in ascending order (x-dimension increments first, y-dimension increments
     * second).
     *
     * @param pointConsumer called for each point.
     */
    public void iterateOverXY(PointTwoDimensionalConsumer pointConsumer) {
        Point2i point = new Point2i();
        for (point.setY(0); point.y() < size.y(); point.incrementY()) {
            for (point.setX(0); point.x() < size.x(); point.incrementX()) {
                pointConsumer.accept(point);
            }
        }
    }

    /**
     * Calls processor once for each x and y-values in the range, with a shift added.
     *
     * <p>This occurs in ascending order (x-dimension increments first, y-dimension increments
     * second).
     *
     * @param shift a shift added to each point, so the effective iteration occurs over @{@code
     *     extent + shift}.
     * @param pointConsumer called for each point.
     */
    public void iterateOverXYWithShift(Point2i shift, PointTwoDimensionalConsumer pointConsumer) {

        int maxX = size.x() + shift.x();
        int maxY = size.y() + shift.y();

        Point2i point = new Point2i();
        for (point.setY(shift.y()); point.y() < maxY; point.incrementY()) {
            for (point.setX(shift.x()); point.x() < maxX; point.incrementX()) {
                pointConsumer.accept(point);
            }
        }
    }

    /**
     * Calls processor once for each x and y-values but <i>only</i> passing an offset.
     *
     * <p>This occurs in ascending order (x-dimension increments first, y-dimension increments
     * second).
     *
     * @param <E> a checked-exception that {@code offsetConsumer} may throw.
     * @param offsetConsumer called for each point with the offset.
     * @throws E if {@code indexConsumer} throws this exception.
     * @throws AnchorFriendlyRuntimeException if the area is too large to be expressed as an {@code
     *     int}.
     */
    public <E extends Exception> void iterateOverXYOffset(CheckedIntConsumer<E> offsetConsumer)
            throws E {
        int maxOffset = areaXYAsInt();
        for (int offset = 0; offset < maxOffset; offset++) {
            offsetConsumer.accept(offset);
        }
    }

    /**
     * Calls processor once for each x and y-values in the range.
     *
     * <p>This occurs in ascending order (x-dimension increments first, y-dimension increments
     * second).
     *
     * @param pointConsumer called for each point.
     */
    public void iterateOverXYOffset(OffsettedPointTwoDimensionalConsumer pointConsumer) {
        int offset = 0;
        Point2i point = new Point2i();
        for (point.setY(0); point.y() < size.y(); point.incrementY()) {
            for (point.setX(0); point.x() < size.x(); point.incrementX()) {
                pointConsumer.accept(point, offset++);
            }
        }
    }

    /**
     * Calls processor once for each x and y-values in the range.
     *
     * <p>This occurs in ascending order (x-dimension increments first, y-dimension increments
     * second).
     *
     * @param pointConsumer called for each point.
     */
    public void iterateOverYXOffset(OffsettedPointTwoDimensionalConsumer pointConsumer) {
        int offset = 0;
        Point2i point = new Point2i();
        for (point.setX(0); point.x() < size.x(); point.incrementX()) {
            for (point.setY(0); point.y() < size.y(); point.incrementY()) {
                pointConsumer.accept(point, offset++);
            }
        }
    }

    /**
     * Calls processor once for each z-value in the range.
     *
     * <p>This occurs sequentially from 0 (inclusive) to {@code z()} (exclusive).
     *
     * @param <E> a checked-exception that {@code indexConsumer} may throw
     * @param indexConsumer called for each index (z-value)
     * @throws E if {@code indexConsumer} throws this exception
     */
    public <E extends Exception> void iterateOverZ(CheckedIntConsumer<E> indexConsumer) throws E {
        for (int z = 0; z < size.z(); z++) {
            indexConsumer.accept(z);
        }
    }

    /**
     * Calls processor once for each z-value in the range unless {@code indexPredicate} returns
     * false.
     *
     * <p>This occurs sequentially from 0 (inclusive) to {@code z()} (exclusive).
     *
     * <p>As soon as the {@code indexPredicate} returns false, the iteration stops.
     *
     * @param indexPredicate called for each index (z-value)
     * @return true if {@code indexPredicate} always returned true for every slice, false otherwise.
     */
    public boolean iterateOverZUntil(IntPredicate indexPredicate) {
        for (int z = 0; z < size.z(); z++) {
            if (!indexPredicate.test(z)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Streams over the range of z values.
     *
     * <p>The values range from 0 (inclusive) to {@code z()} (exclusive).
     *
     * @return the stream.
     */
    public IntStream streamOverZ() {
        return IntStream.range(0, size.z());
    }

    /**
     * Derives an three-element array with each dimension in the extent.
     *
     * @return a newly created three-element array with respectively extents for the x, y and z
     *     dimensions.
     */
    public int[] toArray() {
        return new int[] {x(), y(), z()};
    }

    /**
     * An extent that contains the minimum of two extents for each dimension respectively.
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @param extent the other extent to find a minimum with.
     * @return a newly created extent.
     */
    public Extent minimum(Extent extent) {
        return new Extent(
                Math.min(x(), extent.x()), Math.min(y(), extent.y()), Math.min(z(), extent.z()));
    }

    /**
     * Derives an aspect-ratio, by dividing the X-dimension by the Y-dimension value.
     *
     * <p>Note that the Z-dimension is irrelevant to this calculation.
     *
     * @return The X-dimension divided by the Y-dimension.
     */
    public double aspectRatioXY() {
        return ((double) x()) / y();
    }

    @Override
    public int compareTo(Extent other) {
        return size.compareTo(other.size);
    }

    /**
     * Returns the XY area as int, but only if it fits inside the range of an int, otherwise raising
     * a {@link AnchorFriendlyRuntimeException}.
     */
    private int areaXYAsInt() {
        if (areaXY >= 0) {
            return areaXY;
        } else {
            throw new AnchorFriendlyRuntimeException(
                    "The area is negative, which is physically impossible, and an indication that the area is too large to be stored as an int.");
        }
    }

    /**
     * Calculates the XY area to assign to a variable, or -1 if it is too large to be stored as an
     * int.
     */
    private int calculateAreaXY() {
        double areaXYDouble = ((double) size.x()) * size.y();
        if (areaXYDouble <= Integer.MAX_VALUE) {
            return size.x() * size.y();
        } else {
            return -1;
        }
    }

    private Point3i immutablePointOperation(Consumer<Point3i> pointOperation) {
        Point3i sizeDuplicated = new Point3i(size);
        pointOperation.accept(sizeDuplicated);
        return sizeDuplicated;
    }
}
