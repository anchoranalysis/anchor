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

package org.anchoranalysis.image.core.dimensions;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.spatial.axis.Axis;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.Tuple3d;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * The resolution of an image.
 *
 * <p>i.e. what a single voxel represents in physical units (meters) in x, y, z dimensions.
 *
 * <p>This class is <b>immutable</b>.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Resolution implements Serializable {

    private static final long serialVersionUID = 1L;

    // Stores in meters. If we change this, do we need to update annotations?
    private final Point3d res;

    /** Constructions a default resolution with value 1.0 in each dimension. */
    public Resolution() {
        this.res = new Point3d(1.0, 1.0, 1.0);
    }

    /**
     * Constructions with only XY resolution, identical in both dimensions.
     *
     * <p>Z-resolution is considered unknown.
     *
     * @param xy the resolution in meters for <i>both</i> x and y dimension.
     */
    public static Resolution createWithXY(double xy) {
        try {
            return new Resolution(xy, xy, Double.NaN);
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Constructions with XY resolution and Z resolution.
     *
     * <p>The X and Y resolution is considered identical in both dimensions.
     *
     * @param xy the resolution in meters for <i>both</i> x and y dimension.
     * @param z the resolution in meters for the z dimension.
     */
    public static Resolution createWithXYAndZ(double xy, double z) {
        try {
            return new Resolution(xy, xy, z);
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Constructs a resolution from three double values for each dimension.
     *
     * <p>Note all dimensions must have positive (i.e. non-zero) resolution.
     *
     * <p>X and Y are not allowed have NaN but this is acceptable for the Z-value.
     *
     * @param x the resolution for the X-dimension.
     * @param y the resolution for the Y-dimension.
     * @param z the resolution for the Z-dimension.
     * @throws CreateException if a non-positive value is passed, or a NaN for the X or Y components.
     */
    public Resolution(double x, double y, double z) throws CreateException {
        this(new Point3d(x, y, z));
    }

    /**
     * Constructs a resolution from a tuple.
     *
     * <p>Note all dimensions must have positive (i.e. non-zero) resolution.
     *
     * <p>X and Y are not allowed have NaN but this is acceptable for the Z-value.
     *
     * @param tuple the resolution for X, Y and Z.
     * @throws CreateException if a non-positive value is passed, or a NaN for the X or Y components.
     */
    public Resolution(Tuple3d tuple) throws CreateException {
        checkPositive(tuple.x(), "x");
        checkPositive(tuple.y(), "y");
        checkPositive(tuple.z(), "z");

        checkNaN(tuple.x(), "x");
        checkNaN(tuple.y(), "y");

        // Copy to ensure it is independent of any changes outside
        this.res = new Point3d(tuple);
    }

    public Resolution duplicateFlattenZ(int prevZSize) {
        Preconditions.checkArgument(prevZSize > 0);
        return new Resolution(new Point3d(res.x(), res.y(), res.z() * prevZSize));
    }

    public double x() {
        return res.x();
    }

    public double y() {
        return res.y();
    }

    public double z() {
        return res.z();
    }

    /**
     * Returns the z-resolution like {@link #z()} if the z-resolution is not NaN.
     *
     * <p>Otherwise returns {@code fallback}.
     *
     * @param fallback the value to return if the z-resolution is NaN
     * @return either the z-resolution (if not NaN) or {@code fallback}.
     */
    public double zIfDefined(double fallback) {
        double z = res.z();
        if (!Double.isNaN(z)) {
            return z;
        } else {
            return fallback;
        }
    }

    public double meanXY() {
        return (x() + y()) / 2;
    }

    public double unitVolume() {
        return x() * y() * z();
    }

    public double unitArea() {
        return x() * y();
    }

    public Resolution scaleXY(ScaleFactor scaleFactor) {
        try {
            return new Resolution(res.x() * scaleFactor.x(), res.y() * scaleFactor.y(), res.z());
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    private double max2D() {
        return Math.max(res.x(), res.y());
    }

    private double min2D() {
        return Math.min(res.x(), res.y());
    }

    public double max(boolean do3D) {

        if (do3D) {
            return Math.max(max2D(), res.z());
        } else {
            return max2D();
        }
    }

    public double min(boolean do3D) {
        if (do3D) {
            return Math.min(min2D(), res.z());
        } else {
            return min2D();
        }
    }

    public boolean hasEqualXAndY() {
        return res.x() == res.y();
    }

    /**
     * The square of the distance between two points taking account image-resolution.
     *
     * @param point1 first point.
     * @param point2 second point.
     * @return the square of the distance between the two points.
     */
    public double distanceSquared(Point3i point1, Point3i point2) {
        double sx = (double) point1.x() - point2.x();
        double sy = (double) point1.y() - point2.y();
        double sz = (double) point1.z() - point2.z();
        return sumSquares(sx, sy, sz);
    }

    /**
     * The square of the distance between two points taking account image-resolution.
     *
     * @param point1 first point
     * @param point2 second point
     * @return the square of the distance between the two points
     */
    public double distanceSquared(Point3d point1, Point3d point2) {
        double sx = point1.x() - point2.x();
        double sy = point1.y() - point2.y();
        double sz = point1.z() - point2.z();
        return sumSquares(sx, sy, sz);
    }

    public double distance(Point3d point1, Point3d point2) {
        return Math.sqrt(distanceSquared(point1, point2));
    }

    public double distance(Point3i point1, Point3i point2) {
        return Math.sqrt(distanceSquared(point1, point2));
    }

    public double distanceSquaredZRelative(Point3i point1, Point3i point2) {

        int sx = point1.x() - point2.x();
        int sy = point1.y() - point2.y();
        double sz = ((double) point1.z()) - point2.z();

        return sumSquaresZRelative(sx, sy, sz);
    }

    public double distanceZRelative(Point3d point1, Point3d point2) {
        return Math.sqrt(distanceSquaredZRelative(point1, point2));
    }

    public double distanceSquaredZRelative(Point3d point1, Point3d point2) {

        double sx = point1.x() - point2.x();
        double sy = point1.y() - point2.y();
        double sz = point1.z() - point2.z();

        return sumSquaresZRelative(sx, sy, sz);
    }

    /**
     * Converts voxelized measurements to/from physical units.
     *
     * @return a converter that will perform conversions using current resolution.
     */
    public UnitConverter unitConvert() {
        return new UnitConverter(this);
    }

    /**
     * Assumes x and y has constant resolution, and gives the relative resolution of z to x or y.
     *
     * @return
     */
    public double zRelative() {
        return z() / x();
    }

    @Override
    public String toString() {
        return String.format("[%6.3e,%6.3e,%6.3e]", res.x(), res.y(), res.z());
    }

    public final double valueByDimension(int dimensionIndex) {
        return res.valueByDimension(dimensionIndex);
    }

    public final double valueByDimension(Axis axis) {
        return res.valueByDimension(axis);
    }

    private double sumSquares(double distanceX, double distanceY, double distanceZ) {
        distanceX *= x();
        distanceY *= y();
        distanceZ *= z();
        return Math.pow(distanceX, 2) + Math.pow(distanceY, 2) + Math.pow(distanceZ, 2);
    }

    private double sumSquaresZRelative(double distanceX, double distanceY, double distanceZ) {
        distanceZ *= zRelative();
        return Math.pow(distanceX, 2) + Math.pow(distanceY, 2) + Math.pow(distanceZ, 2);
    }

    private static void checkPositive(double value, String dimension) throws CreateException {
        if (value <= 0) {
            throw new CreateException(
                    String.format("Dimension %s has a non-positive value: %d", dimension, value));
        }
    }

    private static void checkNaN(double value, String dimension) throws CreateException {
        if (Double.isNaN(value)) {
            throw new CreateException(
                    String.format("Dimension %s has NaN which is not allowed.", dimension));
        }
    }
}
