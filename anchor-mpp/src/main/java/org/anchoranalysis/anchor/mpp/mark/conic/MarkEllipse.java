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

package org.anchoranalysis.anchor.mpp.mark.conic;

import static org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities.*;
import static org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers.*;
import static org.anchoranalysis.anchor.mpp.mark.conic.PropertyUtilities.*;
import static org.anchoranalysis.anchor.mpp.mark.conic.TensorUtilities.*;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.DoubleBinaryOperator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkConic;
import org.anchoranalysis.anchor.mpp.mark.QuickOverlapCalculation;
import org.anchoranalysis.anchor.overlay.OverlayProperties;
import org.anchoranalysis.core.geometry.Point2d;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.orientation.Orientation;
import org.anchoranalysis.image.orientation.Orientation2D;
import org.anchoranalysis.math.rotation.RotationMatrix;

public class MarkEllipse extends MarkConic implements Serializable {

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
    public MarkEllipse() {
        super();

        this.radii = new Point2d();

        ellipsoidCalculator = new EllipsoidMatrixCalculator(NUM_DIM_MATRIX);
    }

    // Copy Constructor
    public MarkEllipse(MarkEllipse src) {
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
    public final byte evalPointInside(Point3d pt) {

        if (pt.distanceSquared(this.getPos()) > radiiShellMaxSq) {
            return FLAG_SUBMARK_NONE;
        }

        // It is permissible to mutate the point during calculation
        double x = pt.getX() - getPos().getX();
        double y = pt.getY() - getPos().getY();

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
        return (Math.PI * this.radii.getX() * this.radii.getY() * Math.pow(multiplier, 2));
    }

    // Circumference
    public double circumference(int regionID) {
        if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL) {
            return calcCircumferenceUsingRamunjanApprox(
                    this.radii.getX() * (1.0 + shellRad), this.radii.getY() * (1.0 + shellRad));
        } else {
            return calcCircumferenceUsingRamunjanApprox(this.radii.getX(), this.radii.getY());
        }
    }

    private static double calcCircumferenceUsingRamunjanApprox(double a, double b) {
        // http://www.mathsisfun.com/geometry/ellipse-perimeter.html

        double first = 3 * (a + b);
        double second = ((3 * a) + b) * (a + (3 * b));

        return Math.PI * (first - Math.sqrt(second));
    }

    @Override
    public MarkEllipse duplicate() {
        return new MarkEllipse(this);
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s pos=%s %s vol=%e shellRad=%f",
                "Ellpsd", strId(), strPos(), strMarks(), volume(0), shellRad);
    }

    public void updateShellRad(double shellRad) {
        setShellRad(shellRad);
        updateAfterMarkChange();
    }

    private void updateAfterMarkChange() {

        assert (shellRad > 0);

        DoubleMatrix2D matRot = orientation.createRotationMatrix().getMatrix();

        double[] radiusArray = twoElementArray(this.radii.getX(), this.radii.getY());
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
        Preconditions.checkArgument(pos.getZ() == 0, "non-zero z-value");
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
        setMarksExplicit(pos, orientation, new Point2d(radii.getX(), radii.getY()));
    }

    @Override
    public BoundingBox bbox(ImageDimensions bndScene, int regionID) {

        DoubleMatrix1D bboxMatrix = ellipsoidCalculator.getBoundingBoxMatrix().copy();

        if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL) {
            bboxMatrix.assign(Functions.mult(shellExtOut));
        }

        return BoundingBoxCalculator.bboxFromBounds(getPos(), bboxMatrix, false, bndScene);
    }

    private transient QuickOverlapCalculation quickOverlap =
            (Mark mark, int regionID) -> {
                // No quick tests unless it's the same type of class
                if (!(mark instanceof MarkEllipse)) {
                    return false;
                }

                MarkEllipse trgtMark = (MarkEllipse) mark;

                DoubleMatrix1D relPos =
                        twoElementMatrix(
                                trgtMark.getPos().getX() - getPos().getX(),
                                trgtMark.getPos().getY() - getPos().getY());

                DoubleMatrix1D relPosSquared = relPos.copy();
                relPosSquared.assign(Functions.square); // NOSONAR
                double distance = relPosSquared.zSum();

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
        this.radii.setX(this.radii.getX() * multFactor);
        this.radii.setY(this.radii.getY() * multFactor);
        updateAfterMarkChange();
    }

    // NB objects are scaled in pre-rotated position i.e. when aligned to axes
    @Override
    public void scale(double multFactor) {
        super.scale(multFactor);

        this.radii.setX(this.radii.getX() * multFactor);
        this.radii.setY(this.radii.getY() * multFactor);
        updateAfterMarkChange();
    }

    @Override
    public boolean equalsDeep(Mark m) {

        if (!super.equalsDeep(m)) {
            return false;
        }

        if (!(m instanceof MarkEllipse)) {
            return false;
        }

        MarkEllipse trgt = (MarkEllipse) m;

        if (!radii.equals(trgt.radii)) {
            return false;
        }

        return orientation.equals(trgt.orientation);
    }

    @Override
    public ObjectWithProperties deriveObject(
            ImageDimensions bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bvOut) {

        ObjectWithProperties object = super.deriveObject(bndScene, rm, bvOut);
        orientation.addPropertiesToMask(object);

        // Axis orientation
        addAxisOrientationProperties(object, rm);

        return object;
    }

    @Override
    public OverlayProperties generateProperties(ImageResolution sr) {
        OverlayProperties op = super.generateProperties(sr);

        op.addDoubleAsString("Radius X (pixels)", radii.getX());
        op.addDoubleAsString("Radius Y (pixels)", radii.getY());
        orientation.addProperties(op.getNameValueSet());
        op.addDoubleAsString("Shell Radius (pixels)", shellRad);

        return op;
    }

    @Override
    public int numDims() {
        return 2;
    }

    @Override
    public double[] createRadiiArray() {
        return twoElementArray(this.radii.getX(), this.radii.getY());
    }

    @Override
    public double[] createRadiiArrayResolved(ImageResolution res) {
        return twoElementArray(radii.getX(), radii.getY());
    }

    @Override
    public int numRegions() {
        return 2;
    }

    @Override
    public BoundingBox bboxAllRegions(ImageDimensions bndScene) {
        return bbox(bndScene, GlobalRegionIdentifiers.SUBMARK_SHELL);
    }

    private void addAxisOrientationProperties(
            ObjectWithProperties object, RegionMembershipWithFlags rm) {

        // NOTE can we do this more smartly?
        double radiiFactor = rm.getRegionID() == 0 ? 1.0 : 1.0 + shellRad;

        double radiusProjectedX = radii.getX() * radiiFactor;

        RotationMatrix rotMat = orientation.createRotationMatrix();
        double[] endPoint1 = rotMat.calcRotatedPoint(twoElementArray(-1 * radiusProjectedX));
        double[] endPoint2 = rotMat.calcRotatedPoint(twoElementArray(radiusProjectedX));

        double[] xMinMax = minMaxEndPoint(endPoint1, endPoint2, 0, getPos().getX());
        double[] yMinMax = minMaxEndPoint(endPoint1, endPoint2, 1, getPos().getY());

        addPoint2dProperty(object, "xAxisMin", xMinMax[0], yMinMax[0]);
        addPoint2dProperty(object, "xAxisMax", xMinMax[1], yMinMax[1]);
    }

    private String strMarks() {
        return String.format(
                "rad=[%3.3f, %3.3f] rot=%s",
                this.radii.getX(), this.radii.getY(), this.orientation);
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
