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
import java.util.Arrays;
import java.util.function.Consumer;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.scale.ScaleFactorUtilities;

/**
 * Width, height etc. of image in 2 or 3 dimensions
 *
 * <p>This class is IMMUTABLE
 */
public final class Extent implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    private final int sxy;

    // Lengths in each dimension
    private final ReadableTuple3i len;

    /** Constructor - create with only x and y dimensions (z dimension is assumed to be 1) */
    public Extent(int x, int y) {
        this(x, y, 1);
    }

    /** Constructor - create with x and y and z dimensions */
    public Extent(int x, int y, int z) {
        this(new Point3i(x, y, z));
    }

    /**
     * Constructor
     *
     * <p>The point will be taken ownership by the extent, and should not be modified thereafter.
     *
     * @param len the length of each axis
     */
    private Extent(ReadableTuple3i len) {
        this.len = len;
        this.sxy = len.x() * len.y();

        if (len.x()==0 || len.y()==0 || len.z()==0) {
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
        return ((long) sxy) * len.z();
    }

    public boolean isEmpty() {
        return (sxy == 0) || (len.z() == 0);
    }

    public int volumeXY() {
        return sxy;
    }

    /**
     * Calculates the total number of pixel positions needed to represent this bounding box as a
     * pixel array This is not the same as volume, both the start and end pixel are included
     */
    public int totalNumPixelPositions() {
        return (len.x() + 1) * (len.y() + 1) * (len.z() + 1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + len.x();
        result = prime * result + len.y();
        result = prime * result + len.z();
        return result;
    }

    public int x() {
        return len.x();
    }

    public int y() {
        return len.y();
    }

    public int z() {
        return len.z();
    }

    public int valueByDimension(int dimIndex) {
        return len.byDimension(dimIndex);
    }

    public int valueByDimension(AxisType axis) {
        return len.byDimension(axis);
    }

    /**
     * Exposes the extent as a tuple.
     *
     * <p>IMPORTANT! This class is designed to be IMMUTABLE, so this tuple should be treated as
     * read-only, and never modified.
     *
     * @return the extent's width, height, depth as a tuple
     */
    public ReadableTuple3i asTuple() {
        return len;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Extent other = (Extent) obj;

        return len.equals(other.len);
    }

    @Override
    public String toString() {
        return String.format("[%d,%d,%d]", x(), y(), z());
    }

    /** Calculates a XY-offset of a point in a buffer whose dimensions are this extent */
    public final int offset(int x, int y) {
        return (y * len.x()) + x;
    }

    /** Calculates a XYZ-offset of a point in a buffer whose dimensions are this extent */
    public final int offset(int x, int y, int z) {
        return (z * sxy) + (y * x()) + x;
    }

    /** Calculates a XYZ-offset of a point in a buffer whose dimensions are this extent */
    public final int offset(Point3i point) {
        return offset(point.x(), point.y(), point.z());
    }

    /** Calculates a XY-offset of a point in a buffer whose dimensions are this extent */
    public final int offset(Point2i point) {
        return offset(point.x(), point.y(), 0);
    }

    /** Calculates a XY-offset of a point in a buffer whose dimensions are this extent */
    public final int offsetSlice(Point3i point) {
        return offset(point.x(), point.y(), 0);
    }

    public int[] asOrderedArray() {
        int[] extents = deriveArray();
        Arrays.sort(extents);
        return extents;
    }

    public Extent duplicateChangeZ(int z) {
        return new Extent(len.x(), len.y(), z);
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

        if (x >= len.x()) {
            return false;
        }

        if (y >= len.y()) {
            return false;
        }

        return (z < len.z());
    }

    public boolean contains(BoundingBox box) {
        return contains(box.cornerMin()) && contains(box.calcCornerMax());
    }

    public Extent scaleXYBy(ScaleFactor scaleFactor) {
        return new Extent(
                immutablePointOperation(
                        point -> {
                            point.setX(ScaleFactorUtilities.scaleQuantity(scaleFactor.x(), x()));
                            point.setY(ScaleFactorUtilities.scaleQuantity(scaleFactor.y(), y()));
                        }));
    }

    public Extent subtract(ReadableTuple3i toSubtract) {
        return new Extent(Point3i.immutableSubtract(len, toSubtract));
    }

    public Extent divide(int factor) {
        return new Extent(immutablePointOperation(p -> p.divideBy(factor)));
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
        return new Extent(Point3i.immutableAdd(len, toAdd));
    }
    
    /**
     * Intersects this extent with another (i.e. takes the smaller value in each dimension)
     * 
     * @param other the other
     * @return a newly-created extent that is the intersection of this and another
     */
    public Extent intersectWith(Extent other) {
        return new Extent(
           Point3i.elementwiseOperation(len, other.len, Math::min)
        );
    }
    
    /**
     * Collapses the Z dimension i.e. returns a new extent with the same X- and Y- size but Z-size
     * of 1
     */
    public Extent flattenZ() {
        return new Extent(new Point3i(len.x(), len.y(), 1));
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

    private Point3i immutablePointOperation(Consumer<Point3i> pointOperation) {
        Point3i lenDup = new Point3i(len);
        pointOperation.accept(lenDup);
        return lenDup;
    }
    
    private int[] deriveArray() {
        int[] arr = new int[3];
        arr[0] = x();
        arr[1] = y();
        arr[2] = z();
        return arr;
    }
}
