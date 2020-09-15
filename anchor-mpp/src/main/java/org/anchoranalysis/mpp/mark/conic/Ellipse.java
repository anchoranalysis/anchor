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
import static org.anchoranalysis.mpp.mark.conic.PropertyUtilities.*;
import static org.anchoranalysis.mpp.mark.conic.TensorUtilities.*;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.DoubleBinaryOperator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.geometry.Point2d;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.orientation.Orientation;
import org.anchoranalysis.image.orientation.Orientation2D;
import org.anchoranalysis.math.rotation.RotationMatrix;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.QuickOverlapCalculation;
import org.anchoranalysis.overlay.OverlayProperties;

public class Ellipse extends ConicBase implements Serializable {

    /** */
    private static final long serialVersionUID = -2678275834893266874L;

    private static final int NUM_DIM_MATRIX = 2;

    private static final byte FLAG_SUBMARK_NONE = flagForNoRegion();
    private static final byte FLAG_SUBMARK_REGION0 = flagForRegion(SUBMARK_INSIDE, SUBMARK_CORE);
    private static final byte FLAG_SUBMARK_REGION1 = flagForRegion(SUBMARK_INSIDE, SUBMARK_SHELL);
    private static final byte FLAG_SUBMARK_REGION2 =
            flagForRegion(SUBMARK_SHELL, SUBMARK_SHELL_OUTSIDE);
    private static final byte FLAG_SUBMARK_REGION3 = flagForRegion(SUBMARK_OUTSIDE);

    // START Configurable parameters
    @Getter @Setter private double shellRad = 0.2;
    // END configurable parameters

    // START mark state
    @Getter private Point2d radii;

    @Getter private Orientation orientation = new Orientation2D();
    // END mark state

    // START internal objects
    private EllipsoidMatrixCalculator ellipsoidCalculator;
    private double shellInt;
    private double shellExt;
    private double shellExtOut;

    private double shellIntSq;
    private double shellExtSq;
    private double shellExtOutSq;

    private double radiiShellMaxSq;
    // END internal objects

    // Default Constructor
    public Ellipse() {
        super();

        this.radii = new Point2d();

        ellipsoidCalculator = new EllipsoidMatrixCalculator(NUM_DIM_MATRIX);
    }

    // Copy Constructor
    public Ellipse(Ellipse src) {
        super(src);
        this.radii = new Point2d(src.radii);

        this.shellRad = src.shellRad;

        this.ellipsoidCalculator = new EllipsoidMatrixCalculator(src.ellipsoidCalculator);
        this.orientation = src.orientation.duplicate();

        this.shellExt = src.shellExt;
        this.shellInt = src.shellInt;
        this.shellExtOut = src.shellExtOut;
        this.shellExtSq = src.shellExtSq;
        this.shellIntSq = src.shellIntSq;
        this.shellExtOutSq = src.shellExtOutSq;

        this.radiiShellMaxSq = src.radiiShellMaxSq;
    }

    @Override
    public String getName() {
        return "ellipsoid";
    }

    public static double getEllipseSum(double x, double y, DoubleMatrix2D mat) {
        return x * (x * mat.get(0, 0) + y * mat.get(1, 0))
                + y * (x * mat.get(0, 1) + y * mat.get(1, 1));
    }

    // Where is a point in relation to the current object
    @Override
    public final byte isPointInside(Point3d point) {

        if (point.distanceSquared(this.getPos()) > radiiShellMaxSq) {
            return FLAG_SUBMARK_NONE;
        }

        // It is permissible to mutate the point during calculation
        double x = point.x() - getPos().x();
        double y = point.y() - getPos().y();

        // We exit early if it's inside the internal shell
        double sum = getEllipseSum(x, y, ellipsoidCalculator.getEllipsoidMatrix());

        if (sum <= shellIntSq) {
            return FLAG_SUBMARK_REGION0;
        }

        if (sum <= 1) {
            return FLAG_SUBMARK_REGION1;
        }

        if (sum <= shellExtSq) {
            return FLAG_SUBMARK_REGION2;
        }

        if (sum <= shellExtOutSq) {
            return FLAG_SUBMARK_REGION3;
        }

        return FLAG_SUBMARK_NONE;
    }

    @Override
    public double volume(int regionID) {

        if (regionID == GlobalRegionIdentifiers.SUBMARK_INSIDE) {
            return areaForShell(1);
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL) {
            return areaForShell(shellExt) - areaForShell(shellInt);
        } else {
            assert false;
            return 0.0;
        }
    }

    private double areaForShell(double multiplier) {
        return (Math.PI * this.radii.x() * this.radii.y() * Math.pow(multiplier, 2));
    }

    // Circumference
    public double circumference(int regionID) {
        if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL) {
            return circumferenceUsingRamunjanApprox(
                    this.radii.x() * (1.0 + shellRad), this.radii.y() * (1.0 + shellRad));
        } else {
            return circumferenceUsingRamunjanApprox(this.radii.x(), this.radii.y());
        }
    }

    private static double circumferenceUsingRamunjanApprox(double a, double b) {
        // http://www.mathsisfun.com/geometry/ellipse-perimeter.html

        double first = 3 * (a + b);
        double second = ((3 * a) + b) * (a + (3 * b));

        return Math.PI * (first - Math.sqrt(second));
    }

    @Override
    public Ellipse duplicate() {
        return new Ellipse(this);
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s pos=%s %s vol=%e shellRad=%f",
                "Ellpsd", identifier(), strPos(), strMarks(), volume(0), shellRad);
    }

    public void updateShellRad(double shellRad) {
        setShellRad(shellRad);
        updateAfterMarkChange();
    }

    private void updateAfterMarkChange() {

        assert (shellRad > 0);

        DoubleMatrix2D matRot = orientation.createRotationMatrix().getMatrix();

        double[] radiusArray = twoElementArray(this.radii.x(), this.radii.y());
        this.ellipsoidCalculator.update(radiusArray, matRot);

        this.shellInt = 1.0 - this.shellRad;
        this.shellExt = 1.0 + this.shellRad;
        this.shellExtOut = 1.0 + (this.shellRad * 2);

        this.shellIntSq = squared(shellInt);
        this.shellExtSq = squared(shellExt);
        this.shellExtOutSq = squared(shellExtOut);
        this.radiiShellMaxSq = squared(ellipsoidCalculator.getMaximumRadius() * shellExt);

        assert (!Double.isNaN(this.ellipsoidCalculator.getEllipsoidMatrix().get(0, 0)));
    }

    public void setMarksExplicit(Point3d pos, Orientation orientation, Point2d radii) {
        Preconditions.checkArgument(pos.z() == 0, "non-zero z-value");
        super.setPos(pos);
        this.orientation = orientation;
        this.radii = radii;
        updateAfterMarkChange();
    }

    @Override
    public void setMarksExplicit(Point3d pos) {
        setMarksExplicit(pos, orientation, radii);
    }

    @Override
    public void setMarksExplicit(Point3d pos, Orientation orientation) {
        setMarksExplicit(pos, orientation, radii);
    }

    public void setMarksExplicit(Point3d pos, Orientation orientation, Point3d radii) {
        setMarksExplicit(pos, orientation, new Point2d(radii.x(), radii.y()));
    }

    @Override
    public BoundingBox box(Dimensions dimensions, int regionID) {

        DoubleMatrix1D boxMatrix = ellipsoidCalculator.getBoundingBoxMatrix().copy();

        if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL) {
            boxMatrix.assign(Functions.mult(shellExtOut));
        }

        return BoundingBoxCalculator.boxFromBounds(getPos(), boxMatrix, false, dimensions);
    }

    private transient QuickOverlapCalculation quickOverlap =
            (Mark mark, int regionID) -> {
                // No quick tests unless it's the same type of class
                if (!(mark instanceof Ellipse)) {
                    return false;
                }

                Ellipse trgtMark = (Ellipse) mark;

                DoubleMatrix1D relativePosition =
                        twoElementMatrix(
                                trgtMark.getPos().x() - getPos().x(),
                                trgtMark.getPos().y() - getPos().y());

                DoubleMatrix1D relativePositionSquared = relativePosition.copy();
                relativePositionSquared.assign(Functions.square); // NOSONAR
                double distance = relativePositionSquared.zSum();

                // Definitely outside
                return distance > Math.pow(getMaximumRadius() + trgtMark.getMaximumRadius(), 2.0);
            };

    @Override
    public Optional<QuickOverlapCalculation> quickOverlap() {
        return Optional.of(quickOverlap);
    }

    private double getMaximumRadius() {
        return ellipsoidCalculator.getMaximumRadius();
    }

    public void setMarks(Point2d radii, Orientation orientation) {
        this.orientation = orientation;
        this.radii = radii;
        updateAfterMarkChange();
    }

    public void scaleRadii(double multFactor) {
        this.radii.scale(multFactor);
        updateAfterMarkChange();
    }

    // NB objects are scaled in pre-rotated position i.e. when aligned to axes
    @Override
    public void scale(double scaleFactor) {
        super.scale(scaleFactor);
        this.radii.scale(scaleFactor);
        updateAfterMarkChange();
    }

    @Override
    public boolean equalsDeep(Mark m) {

        if (!super.equalsDeep(m)) {
            return false;
        }

        if (!(m instanceof Ellipse)) {
            return false;
        }

        Ellipse trgt = (Ellipse) m;

        if (!radii.equals(trgt.radii)) {
            return false;
        }

        return orientation.equals(trgt.orientation);
    }

    @Override
    public ObjectWithProperties deriveObject(
            Dimensions bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bvOut) {

        ObjectWithProperties object = super.deriveObject(bndScene, rm, bvOut);
        orientation.addPropertiesToMask(object);

        // Axis orientation
        addAxisOrientationProperties(object, rm);

        return object;
    }

    @Override
    public OverlayProperties generateProperties(Resolution sr) {
        OverlayProperties op = super.generateProperties(sr);

        op.addDoubleAsString("Radius X (pixels)", radii.x());
        op.addDoubleAsString("Radius Y (pixels)", radii.y());
        orientation.addProperties(op.getNameValueSet());
        op.addDoubleAsString("Shell Radius (pixels)", shellRad);

        return op;
    }

    @Override
    public int numberDimensions() {
        return 2;
    }

    @Override
    public double[] createRadiiArray() {
        return twoElementArray(this.radii.x(), this.radii.y());
    }

    @Override
    public double[] createRadiiArrayResolved(Resolution resolution) {
        return twoElementArray(radii.x(), radii.y());
    }

    @Override
    public int numberRegions() {
        return 2;
    }

    @Override
    public BoundingBox boxAllRegions(Dimensions dimensions) {
        return box(dimensions, GlobalRegionIdentifiers.SUBMARK_SHELL);
    }

    private void addAxisOrientationProperties(
            ObjectWithProperties object, RegionMembershipWithFlags region) {

        // NOTE can we do this more smartly?
        double radiiFactor = region.getRegionID() == 0 ? 1.0 : 1.0 + shellRad;

        double radiusProjectedX = radii.x() * radiiFactor;

        RotationMatrix rotMat = orientation.createRotationMatrix();
        double[] endPoint1 = rotMat.rotatedPoint(twoElementArray(-1 * radiusProjectedX));
        double[] endPoint2 = rotMat.rotatedPoint(twoElementArray(radiusProjectedX));

        double[] xMinMax = minMaxEndPoint(endPoint1, endPoint2, 0, getPos().x());
        double[] yMinMax = minMaxEndPoint(endPoint1, endPoint2, 1, getPos().y());

        addPoint2dProperty(object, "xAxisMin", xMinMax[0], yMinMax[0]);
        addPoint2dProperty(object, "xAxisMax", xMinMax[1], yMinMax[1]);
    }

    private String strMarks() {
        return String.format(
                "rad=[%3.3f, %3.3f] rot=%s", this.radii.x(), this.radii.y(), this.orientation);
    }

    private static double[] minMaxEndPoint(
            double[] endPoint1, double[] endPoint2, int dimIndex, double toAdd) {
        return twoElementArray(
                applyEndPoint(endPoint1, endPoint2, dimIndex, toAdd, Math::min),
                applyEndPoint(endPoint1, endPoint2, dimIndex, toAdd, Math::max));
    }

    private static double applyEndPoint(
            double[] endPoint1,
            double[] endPoint2,
            int dimIndex,
            double toAdd,
            DoubleBinaryOperator func) {
        return func.applyAsDouble(endPoint1[dimIndex], endPoint2[dimIndex]) + toAdd;
    }
}
