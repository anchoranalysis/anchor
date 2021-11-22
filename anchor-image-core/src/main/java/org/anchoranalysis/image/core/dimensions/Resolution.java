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

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.spatial.axis.Axis;
import org.anchoranalysis.spatial.axis.AxisConverter;
import org.anchoranalysis.spatial.point.Point3d;
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

    /**
     * The resolution in each dimension, stored in meter units.
     *
     * <p>If we change this, this may break some serialized annotations.
     */
    private final Point3d res;

    /** Creates with a default resolution with value 1.0 in each dimension. */
    public Resolution() {
        this.res = new Point3d(1.0, 1.0, 1.0);
    }

    /**
     * Creates with only XY resolution, identical in both dimensions.
     *
     * <p>Z-resolution is considered unknown.
     *
     * @param xy the resolution in meters for <i>both</i> x and y dimension.
     * @return the resolution.
     */
    public static Resolution createWithXY(double xy) {
        try {
            return new Resolution(xy, xy, Double.NaN);
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Creates with XY resolution and Z resolution.
     *
     * <p>The X and Y resolution is considered identical in both dimensions.
     *
     * @param xy the resolution in meters for <i>both</i> x and y dimension.
     * @param z the resolution in meters for the z dimension.
     * @return the resolution.
     */
    public static Resolution createWithXYAndZ(double xy, double z) {
        try {
            return new Resolution(xy, xy, z);
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Creates with a resolution from three double values for each dimension.
     *
     * <p>Note all dimensions must have positive (i.e. non-zero) resolution.
     *
     * <p>X and Y are not allowed have NaN but this is acceptable for the Z-value.
     *
     * @param x the resolution for the X-dimension.
     * @param y the resolution for the Y-dimension.
     * @param z the resolution for the Z-dimension.
     * @throws CreateException if a non-positive value is passed, or a {@link Double#NaN} for the X
     *     or Y components.
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
     * @throws CreateException if a non-positive value is passed, or a {@link Double#NaN} for the X
     *     or Y components.
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

    /**
     * The resolution in the X-dimension in meters.
     *
     * @return the resolution in meters.
     */
    public double x() {
        return res.x();
    }

    /**
     * The resolution in the Y-dimension in meters.
     *
     * @return the resolution in meters.
     */
    public double y() {
        return res.y();
    }

    /**
     * The resolution in the Z-dimension in meters.
     *
     * @return the resolution in meters, or {@link Double#NaN} if it is not defined.
     */
    public double z() {
        return res.z();
    }

    /**
     * Returns the z-resolution like {@link #z()} if the z-resolution is not {@link Double#NaN}.
     *
     * <p>Otherwise returns {@code fallback}.
     *
     * @param fallback the value to return if the z-resolution is {@link Double#NaN}.
     * @return either the z-resolution (if not {@link Double#NaN}) or {@code fallback}.
     */
    public double zIfDefined(double fallback) {
        double z = res.z();
        if (!Double.isNaN(z)) {
            return z;
        } else {
            return fallback;
        }
    }

    /**
     * Multiplying the X- and Y- and Z- resolution values together.
     *
     * @return the volume in cubic meters, or {@link Double#NaN} if the Z-resolution is undefined.
     */
    public double unitVolume() {
        return x() * y() * z();
    }

    /**
     * Multiplying the X- and Y- resolution values together.
     *
     * @return the area in square meters.
     */
    public double unitArea() {
        return x() * y();
    }

    /**
     * Multiplies the X and Y components of the resolution by a factor.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param scaleFactor the factor to multiply each dimension by.
     * @return a newly created {@link Resolution} that is the result of the scaling.
     */
    public Resolution scaleXY(ScaleFactor scaleFactor) {
        try {
            return new Resolution(res.x() * scaleFactor.x(), res.y() * scaleFactor.y(), res.z());
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
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
     * Gives the relative resolution of the z-component to the x-component.
     *
     * @return the z-component divided by the x-component.
     */
    public double zRelative() {
        return z() / x();
    }

    @Override
    public String toString() {
        return String.format("[%6.3e,%6.3e,%6.3e]", res.x(), res.y(), res.z());
    }

    /**
     * A component of a resolution corresponding to a particular dimension by index.
     *
     * @param dimensionIndex the index corresponding to an axis, as per {@link AxisConverter}.
     * @return the component of the tuple corresponding to that axis.
     */
    public final double valueByDimension(int dimensionIndex) {
        return res.valueByDimension(dimensionIndex);
    }

    /**
     * A component of the tuple corresponding to a particular axis.
     *
     * @param axis the axis.
     * @return the component of the tuple corresponding to that axis.
     */
    public final double valueByDimension(Axis axis) {
        return res.valueByDimension(axis);
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
