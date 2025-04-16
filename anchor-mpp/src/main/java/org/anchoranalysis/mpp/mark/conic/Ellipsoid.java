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

package org.anchoranalysis.mpp.mark.conic;

import static org.anchoranalysis.mpp.bean.regionmap.RegionMembershipUtilities.*;
import static org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers.*;
import static org.anchoranalysis.mpp.mark.conic.TensorUtilities.*;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;
import java.io.Serializable;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.exception.CheckedUnsupportedOperationException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.QuickOverlapCalculation;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.orientation.Orientation;
import org.anchoranalysis.spatial.orientation.Orientation3DEulerAngles;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * Represents a 3D ellipsoid mark with multiple sub-regions.
 *
 * <p>The ellipsoid has the following sub-marks:
 * <ul>
 *   <li>Sub-Mark 0: Center Ellipsoid (inner core)</li>
 *   <li>Sub-Mark 1: Ellipsoid with shell</li>
 * </ul>
 * </p>
 */
public class Ellipsoid extends ConicBase implements Serializable {

    private static final long serialVersionUID = -2678275834893266874L;

    private static final int NUM_DIM = 3;

    private static final byte FLAG_SUBMARK_NONE = flagForNoRegion();
    private static final byte FLAG_SUBMARK_REGION0 =
            flagForRegion(SUBMARK_INSIDE, SUBMARK_CORE, SUBMARK_CORE_INNER);
    private static final byte FLAG_SUBMARK_REGION1 = flagForRegion(SUBMARK_INSIDE, SUBMARK_CORE);
    private static final byte FLAG_SUBMARK_REGION2 = flagForRegion(SUBMARK_INSIDE, SUBMARK_SHELL);
    private static final byte FLAG_SUBMARK_REGION3 =
            flagForRegion(SUBMARK_SHELL, SUBMARK_SHELL_OUTSIDE);
    private static final byte FLAG_SUBMARK_REGION4 = flagForRegion(SUBMARK_OUTSIDE);

    /** The size of the shell, expressed as a ratio of the radii. */
    @Getter @Setter private double shell = 0.1;

    /** The distance to the inner core, expressed as a ratio of the radii. */
    @Getter @Setter private double innerCoreDistance = 0.4;

    /** The radii of the ellipsoid in 3D. */
    @Getter private Point3d radii;

    /** The orientation of the ellipsoid. */
    @Getter private Orientation orientation = new Orientation3DEulerAngles(0.0, 0.0, 0.0);

    /** Calculator for ellipsoid-related matrices. */
    @Getter private EllipsoidMatrixCalculator ellipsoidCalculator;

    // Relative distances to various shells squared (expressed as a ratio of the radii)
    private double shellInnerCore;
    private double shellInternal;
    private double shellExternal;
    private double shellExternalOut;

    // Relative distances to various shells squared (expressed as a ratio of the radii squared)
    private double shellInnerCoreSquared;
    private double shellInternalSquared;
    private double shellExternalSquared;
    private double shellExternalOutSquared;

    private double radiiShellMaxSq;

    /**
     * Creates a new Ellipsoid with default values.
     */
    public Ellipsoid() {
        super();
        this.radii = new Point3d();
        ellipsoidCalculator = new EllipsoidMatrixCalculator(NUM_DIM);
    }

    /**
     * Creates a new Ellipsoid by copying an existing one.
     *
     * @param src the Ellipsoid to copy from
     */
    public Ellipsoid(Ellipsoid src) {
        super(src);
        this.radii = new Point3d(src.radii);

        this.shell = src.shell;
        this.innerCoreDistance = src.innerCoreDistance;

        ellipsoidCalculator = new EllipsoidMatrixCalculator(src.ellipsoidCalculator);

        this.orientation = src.orientation;
        this.radiiShellMaxSq = src.radiiShellMaxSq;

        this.shellExternal = src.shellExternal;
        this.shellExternalOut = src.shellExternalOut;
        this.shellInternal = src.shellInternal;
        this.shellInnerCore = src.shellInnerCore;

        this.shellExternalSquared = src.shellExternalSquared;
        this.shellExternalOutSquared = src.shellExternalOutSquared;
        this.shellInternalSquared = src.shellInternalSquared;
        this.shellInnerCoreSquared = src.shellInnerCoreSquared;
    }

    @Override
    public String getName() {
        return "ellipsoid";
    }

    /**
     * Calculates the sum of squared coordinates relative to the ellipsoid's matrix.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param mat the ellipsoid matrix
     * @return the sum of squared coordinates
     */
    public static double getEllipsoidSum(double x, double y, double z, DoubleMatrix2D mat) {
        return x * (x * mat.get(0, 0) + y * mat.get(1, 0) + z * mat.get(2, 0))
                + y * (x * mat.get(0, 1) + y * mat.get(1, 1) + z * mat.get(2, 1))
                + z * (x * mat.get(0, 2) + y * mat.get(1, 2) + z * mat.get(2, 2));
    }

    private static double l2norm(double x, double y, double z) {
        return Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0);
    }

    // Where is a point in relation to the current object
    @Override
    public final byte isPointInside(Point3i point) {

        // Add in 0.5 to take the center-point of the voxel identified by point
        double x = point.x() - getPosition().x() + 0.5;
        double y = point.y() - getPosition().y() + 0.5;
        double z = point.z() - getPosition().z() + 0.5;

        if (l2norm(x, y, z) > radiiShellMaxSq) {
            return FLAG_SUBMARK_NONE;
        }

        // We exit early if it's inside the internal shell
        double sum = getEllipsoidSum(x, y, z, ellipsoidCalculator.getEllipsoidMatrix());
        if (sum <= shellInnerCoreSquared) {
            return FLAG_SUBMARK_REGION0;
        }

        // We exit early if it's inside the internal shell
        sum = getEllipsoidSum(x, y, z, ellipsoidCalculator.getEllipsoidMatrix());
        if (sum <= shellInternalSquared) {
            return FLAG_SUBMARK_REGION1;
        }

        if (sum <= 1) {
            return FLAG_SUBMARK_REGION2;
        }

        if (sum <= shellExternalSquared) {
            return FLAG_SUBMARK_REGION3;
        }

        if (sum <= shellExternalOutSquared) {
            return FLAG_SUBMARK_REGION4;
        }

        return FLAG_SUBMARK_NONE;
    }

    @Override
    public Mark duplicate() {
        return new Ellipsoid(this);
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s pos=%s %s vol=%e", "Ellpsd", identifier(), positionString(), strMarks(), volume(0));
    }

    @Override
    public double volume(int regionID) {

        if (regionID == GlobalRegionIdentifiers.SUBMARK_INSIDE) {
            return volumeForShell(1);
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL_OUTSIDE) {
            return volumeForShell(shellExternal) - volumeForShell(1);
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL) {
            return volumeForShell(shellExternal) - volumeForShell(shellInternal);
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_CORE) {
            return volumeForShell(shellInternal);
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_OUTSIDE) {
            return volumeForShell(shellExternalOut) - volumeForShell(shellExternal);
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_CORE_INNER) {
            return volumeForShell(shellInnerCore);
        } else {
            assert false;
            return 0.0;
        }
    }

    /**
     * Calculates the volume for a specific shell of the ellipsoid.
     *
     * @param multiplier the multiplier for the shell
     * @return the volume of the shell
     */
    private double volumeForShell(double multiplier) {
        return (4
                        * Math.PI
                        * this.radii.x()
                        * this.radii.y()
                        * this.radii.z()
                        * Math.pow(multiplier, 3))
                / 3;
    }

    /**
     * Updates internal calculations after a change in the mark's properties.
     */
    public void updateAfterMarkChange() {

        DoubleMatrix2D matRot = orientation.getRotationMatrix().getMatrix();

        double[] radiusArray = threeElementArray(this.radii.x(), this.radii.y(), this.radii.z());
        assert matRot.rows() == 3;
        this.ellipsoidCalculator.update(radiusArray, matRot);

        this.shellInternal = 1.0 - this.shell;
        this.shellExternal = 1.0 + this.shell;
        this.shellExternalOut = 1.0 + (this.shell * 2);
        this.shellInnerCore = 1.0 - innerCoreDistance;

        this.shellInternalSquared = squared(shellInternal);
        this.shellExternalSquared = squared(shellExternal);
        this.shellExternalOutSquared = squared(shellExternalOut);
        this.shellInnerCoreSquared = squared(shellInnerCore);

        this.radiiShellMaxSq = squared(ellipsoidCalculator.getMaximumRadius() * shellExternalOut);

        assert shellInternal > 0;
    }

    @Override
    public BoundingBox box(Dimensions dimensions, int regionID) {

        DoubleMatrix1D s = ellipsoidCalculator.getBoundingBoxMatrix().copy();

        assert shellInternal > 0;
        assert shellInnerCore > 0;

        if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL
                || regionID == GlobalRegionIdentifiers.SUBMARK_SHELL_OUTSIDE) {
            s.assign(Functions.mult(shellExternal));
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_CORE) {
            s.assign(Functions.mult(shellInternal));
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_OUTSIDE) {
            s.assign(Functions.mult(shellExternalOut));
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_CORE_INNER) {
            s.assign(Functions.mult(shellInnerCore));
        }

        return BoundingBoxCalculator.boxFromBounds(getPosition(), s, true, dimensions);
    }

    private String strMarks() {
        return String.format(
                "rad=[%3.3f, %3.3f, %3.3f] rot=[%s] shell=[%f]",
                this.radii.x(), this.radii.y(), this.radii.z(), this.orientation.toString(), shell);
    }

    @SuppressWarnings("static-access")
    private transient QuickOverlapCalculation quickOverlap =
            (Mark mark, int regionID) -> {
                // No quick tests unless it's the same type of class
                if (!(mark instanceof Ellipsoid)) {
                    return false;
                }

                Ellipsoid target = (Ellipsoid) mark;

                DoubleMatrix1D relativePosition =
                        TensorUtilities.threeElementMatrix(
                                target.getPosition().x() - getPosition().x(),
                                target.getPosition().y() - getPosition().y(),
                                target.getPosition().z() - getPosition().z());

                DoubleMatrix1D relativePositionSquared = relativePosition.copy();
                relativePositionSquared.assign(Functions.functions.square); // NOSONAR
                double distance = relativePositionSquared.zSum();

                // Definitely outside
                return distance
                        > Math.pow(
                                getMaximumRadius(regionID) + target.getMaximumRadius(regionID),
                                2.0);
            };

    @Override
    public Optional<QuickOverlapCalculation> quickOverlap() {
        return Optional.of(quickOverlap);
    }

    @Override
    public void setMarksExplicit(Point3d pos, Orientation orientation, Point3d radii) {
        super.setPosition(pos);
        this.orientation = orientation;
        this.radii = radii;
        updateAfterMarkChange();
        assert shellInternal > 0;
    }

    @Override
    public void setMarksExplicit(Point3d position) {
        super.setPosition(position);
        updateAfterMarkChange();
    }

    @Override
    public double[] createRadiiArray() {
        return threeElementArray(this.radii.x(), this.radii.y(), this.radii.z());
    }

    @Override
    public double[] createRadiiArrayResolved(Optional<Resolution> resolution) {
        return EllipsoidUtilities.normalisedRadii(this, resolution);
    }

    // NB objects are scaled in pre-rotated position i.e. when aligned to axes
    @Override
    public void scale(ScaleFactor scaleFactor) throws CheckedUnsupportedOperationException {
        super.scale(scaleFactor);
        ScaleChecker.checkIdenticalXY(scaleFactor);
        this.radii.setX(this.radii.x() * scaleFactor.x());
        this.radii.setY(this.radii.y() * scaleFactor.x());
        this.radii.setZ(this.radii.z() * scaleFactor.x());
        updateAfterMarkChange();
    }

    @Override
    public boolean equalsDeep(Mark m) {

        if (!super.equalsDeep(m)) {
            return false;
        }

        if (!(m instanceof Ellipsoid)) {
            return false;
        }

        Ellipsoid trgt = (Ellipsoid) m;

        if (!radii.equals(trgt.radii)) {
            return false;
        }

        return orientation.equals(trgt.orientation);
    }

    @Override
    public int numberDimensions() {
        return 3;
    }

    @Override
    public void setMarksExplicit(Point3d position, Orientation orientation) {
        setMarksExplicit(position, orientation, radii);
    }

    @Override
    public int numberRegions() {
        return 5;
    }

    @Override
    public BoundingBox boxAllRegions(Dimensions dimensions) {
        return box(dimensions, GlobalRegionIdentifiers.SUBMARK_OUTSIDE);
    }

    private double getMaximumRadius(int regionID) {

        double maxRadius = ellipsoidCalculator.getMaximumRadius();

        if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL) {
            maxRadius *= (1 + shell);
        }

        return maxRadius;
    }
}