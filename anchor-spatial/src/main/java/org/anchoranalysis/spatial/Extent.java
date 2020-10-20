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

package org.anchoranalysis.spatial;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.functional.checked.CheckedIntConsumer;
import org.anchoranalysis.spatial.axis.AxisType;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point2i;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.anchoranalysis.spatial.point.consumer.OffsettedPointTwoDimensionalConsumer;
import org.anchoranalysis.spatial.point.consumer.OffsettedScalarTwoDimensionalConsumer;
import org.anchoranalysis.spatial.point.consumer.PointTwoDimensionalConsumer;
import org.anchoranalysis.spatial.scale.ScaleFactor;
import org.anchoranalysis.spatial.scale.ScaleFactorUtilities;

/**
 * Width, height etc. of image in 2 or 3 dimensions.
 *
 * <p>This class is <b>immutable</b>.
 */
public final class Extent implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    /** Sizes in each dimension */
    private final ReadableTuple3i size;

    /** Size in X multiplied by size in Y. Convenient for calculating offsets and for iterations. */
    private final int areaXY;

    /** Creates with with only x and y dimensions (z dimension is assumed to be 1) */
    public Extent(int x, int y) {
        this(x, y, 1);
    }

    /** Creates with x and y and z dimensions */
    public Extent(int x, int y, int z) {
        this(new Point3i(x, y, z));
    }

    /**
     * Createss an extent from a point (duplicating the point for internal use)
     *
     * <p>This constructor is exposed as a static method to deliberately duplicate the tuple as it
     * will be used internally.
     *
     * @param tuple a tuple with the extent size's for each dimension
     * @return a newly created extent - that doesn't reuse {@code tuple} internally
     */
    public static Extent createFromTupleDuplicate(ReadableTuple3i tuple) {
        return new Extent(new Point3i(tuple));
    }

    /**
     * Creates from a point that is reused internally (without duplication)
     *
     * <p>This constructor is exposed as a static method to deliberately indicate that it's okay to
     * consume the point internally, as it won't be otherwise use.
     *
     * @param tuple a tuple with the extent size's for each dimension
     * @return a newly created extent - that reuses {@code tuple} internally
     */
    public static Extent createFromTupleReuse(ReadableTuple3i tuple) {
        return new Extent(new Point3i(tuple));
    }

    /**
     * Creates from a tuple that is reused internally (without duplication).
     *
     * @param len a tuple with the extent size's for each dimension
     */
    private Extent(ReadableTuple3i len) {
        this.size = len;
        this.areaXY = len.x() * len.y();

        if (len.x() == 0 || len.y() == 0 || len.z() == 0) {
            throw new AnchorFriendlyRuntimeException(
                    "An extent must have at least one voxel in every dimension");
        }

        if (len.x() < 0 || len.y() < 0 || len.z() < 0) {
            throw new AnchorFriendlyRuntimeException(
                    "An extent may not be negative in any dimension");
        }
    }

    public int calculateVolumeAsInt() {
        long volume = calculateVolume();
        if (volume > Integer.MAX_VALUE) {
            throw new AnchorFriendlyRuntimeException(
                    "The volume cannot be expressed as an int, as it is higher than the maximum bound");
        }
        return (int) volume;
    }

    public long calculateVolume() {
        return ((long) areaXY) * size.z();
    }

    public boolean isEmpty() {
        return (areaXY == 0) || (size.z() == 0);
    }

    public int volumeXY() {
        return areaXY;
    }

    /**
     * Calculates the total number of pixel positions needed to represent this bounding box as a
     * pixel array This is not the same as volume, both the start and end pixel are included
     */
    public int totalNumberVoxelPositions() {
        return (size.x() + 1) * (size.y() + 1) * (size.z() + 1);
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

    public int x() {
        return size.x();
    }

    public int y() {
        return size.y();
    }

    public int z() {
        return size.z();
    }

    public int valueByDimension(int dimIndex) {
        return size.byDimension(dimIndex);
    }

    public int valueByDimension(AxisType axis) {
        return size.byDimension(axis);
    }

    /**
     * Exposes the extent as a tuple.
     *
     * <p>IMPORTANT! This class is designed to be <b>immutable</b>, so this tuple should be treated
     * as read-only, and never modified.
     *
     * @return the extent's width, height, depth as a tuple
     */
    public ReadableTuple3i asTuple() {
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Extent other = (Extent) obj;

        return size.equals(other.size);
    }

    /** Checks for equality with another extent ignoring any differences in the Z dimension */
    public boolean equalsIgnoreZ(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Extent other = (Extent) obj;

        return (size.x() == other.x()) && (size.y() == other.y());
    }

    @Override
    public String toString() {
        return String.format("[%d,%d,%d]", x(), y(), z());
    }

    /** Calculates a XY-offset of a point in a buffer whose dimensions are this extent */
    public final int offset(int x, int y) {
        return (y * size.x()) + x;
    }

    /** Calculates a XYZ-offset of a point in a buffer whose dimensions are this extent */
    public final int offset(int x, int y, int z) {
        return (z * areaXY) + (y * x()) + x;
    }

    /** Calculates a XYZ-offset of a point in a buffer whose dimensions are this extent */
    public final int offset(ReadableTuple3i point) {
        return offset(point.x(), point.y(), point.z());
    }

    /** Calculates a XY-offset of a point in a buffer whose dimensions are this extent */
    public final int offset(Point2i point) {
        return offset(point.x(), point.y(), 0);
    }

    /** Calculates a XY-offset of a point in a buffer whose dimensions are this extent */
    public final int offsetSlice(ReadableTuple3i point) {
        return offset(point.x(), point.y(), 0);
    }

    public int[] asOrderedArray() {
        int[] extents = deriveArray();
        Arrays.sort(extents);
        return extents;
    }

    public Extent duplicateChangeZ(int z) {
        return new Extent(size.x(), size.y(), z);
    }

    public boolean containsX(double x) {
        return x >= 0 && x < x();
    }

    public boolean containsY(double y) {
        return y >= 0 && y < y();
    }

    public boolean containsZ(double z) {
        return z >= 0 && z < z();
    }

    public boolean containsX(int x) {
        return x >= 0 && x < x();
    }

    public boolean containsY(int y) {
        return y >= 0 && y < y();
    }

    public boolean containsZ(int z) {
        return z >= 0 && z < z();
    }

    public boolean contains(Point2i point) {
        return containsX(point.x()) && containsY(point.y());
    }

    public boolean contains(Point3d point) {
        return containsX(point.x()) && containsY(point.y()) && containsZ(point.z());
    }

    public boolean contains(ReadableTuple3i point) {
        return containsX(point.x()) && containsY(point.y()) && containsZ(point.z());
    }

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

    public boolean contains(BoundingBox box) {
        return contains(box.cornerMin()) && contains(box.calculateCornerMax());
    }

    public Extent scaleXYBy(ScaleFactor scaleFactor) {
        return new Extent(
                immutablePointOperation(
                        point -> {
                            point.setX(ScaleFactorUtilities.scaleQuantity(scaleFactor.x(), x()));
                            point.setY(ScaleFactorUtilities.scaleQuantity(scaleFactor.y(), y()));
                        }));
    }

    public Point3i subtract(ReadableTuple3i toSubtract) {
        return Point3i.immutableSubtract(size, toSubtract);
    }

    public Extent divide(int factor) {
        return new Extent(immutablePointOperation(point -> point.divideBy(factor)));
    }

    /**
     * Creates a new Extent with each dimension decreased by one
     *
     * @return the new extent
     */
    public Point3i createMinusOne() {
        return immutablePointOperation(p -> p.subtract(1));
    }

    public Extent growBy(int toAdd) {
        return growBy(new Point3i(toAdd, toAdd, toAdd));
    }

    public Extent growBy(ReadableTuple3i toAdd) {
        return new Extent(Point3i.immutableAdd(size, toAdd));
    }

    /**
     * Intersects this extent with another (i.e. takes the smaller value in each dimension)
     *
     * @param other the other
     * @return a newly-created extent that is the intersection of this and another
     */
    public Extent intersectWith(Extent other) {
        return new Extent(Point3i.elementwiseOperation(size, other.size, Math::min));
    }

    /**
     * Collapses the Z dimension i.e. returns a new extent with the same X- and Y- size but Z-size
     * of 1
     */
    public Extent flattenZ() {
        return new Extent(new Point3i(size.x(), size.y(), 1));
    }

    /**
     * Returns true if any dimension in this extent is larger than the corresponding dimension in
     * {@code other} extent
     *
     * @param other extent to compare to
     * @return true or false (if all dimensions or less than or equal to their corresponding
     *     dimension in {@code other})
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
     * second)
     *
     * @param <E> a checked-exception that {@code indexConsumer} may throw
     * @param pointConsumer called for each point
     * @throws E if {@code indexConsumer} throws this exception
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
     * @param pointConsumer called for each point
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
     * @param pointConsumer called for each point
     */
    public void iterateOverXYWithShift(Point2i shift, PointTwoDimensionalConsumer pointConsumer) {

        int maxX = size.x() + shift.x();
        int maxY = size.y() + shift.y();

        Point2i point = new Point2i();
        for (point.setY(shift.y()); point.y() < maxX; point.incrementY()) {
            for (point.setX(shift.x()); point.x() < maxY; point.incrementX()) {
                pointConsumer.accept(point);
            }
        }
    }

    /**
     * Calls processor once for each x and y-values but <i>only</i> passing an offset.
     *
     * <p>This occurs in ascending order (x-dimension increments first, y-dimension increments
     * second)
     *
     * @param <E> a checked-exception that {@code offsetConsumer} may throw
     * @param offsetConsumer called for each point with the offset
     * @throws E if {@code indexConsumer} throws this exception
     */
    public <E extends Exception> void iterateOverXYOffset(CheckedIntConsumer<E> offsetConsumer)
            throws E {
        for (int offset = 0; offset < areaXY; offset++) {
            offsetConsumer.accept(offset);
        }
    }

    /**
     * Calls processor once for each x and y-values in the range.
     *
     * <p>This occurs in ascending order (x-dimension increments first, y-dimension increments
     * second)
     *
     * @param pointConsumer called for each point
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
     * Calls processor once for each z-value in the range
     *
     * <p>This occurs sequentially from 0 (inclusive) to {@code z()} (exclusive)
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
     * Streams over the range of z values
     *
     * <p>The values range from 0 (inclusive) to {@code z()} (exclusive).
     *
     * @return the stream
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
    public int[] deriveArray() {
        int[] arr = new int[3];
        arr[0] = x();
        arr[1] = y();
        arr[2] = z();
        return arr;
    }

    private Point3i immutablePointOperation(Consumer<Point3i> pointOperation) {
        Point3i lenDup = new Point3i(size);
        pointOperation.accept(lenDup);
        return lenDup;
    }
}
