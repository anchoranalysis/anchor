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
import com.google.common.base.Preconditions;
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
import org.anchoranalysis.overlay.OverlayProperties;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.orientation.Orientation;
import org.anchoranalysis.spatial.orientation.Orientation2D;
import org.anchoranalysis.spatial.point.Point2d;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

public class Ellipse extends ConicBase implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    private static final int NUMBER_DIMENSIONS = 2;

    private static final byte FLAG_SUBMARK_NONE = flagForNoRegion();
    private static final byte FLAG_SUBMARK_REGION0 = flagForRegion(SUBMARK_INSIDE, SUBMARK_CORE);
    private static final byte FLAG_SUBMARK_REGION1 = flagForRegion(SUBMARK_INSIDE, SUBMARK_SHELL);
    private static final byte FLAG_SUBMARK_REGION2 =
            flagForRegion(SUBMARK_SHELL, SUBMARK_SHELL_OUTSIDE);
    private static final byte FLAG_SUBMARK_REGION3 = flagForRegion(SUBMARK_OUTSIDE);

    private transient QuickOverlapCalculation quickOverlap =
            (Mark mark, int regionID) -> {
                // No quick tests unless it's the same type of class
                if (!(mark instanceof Ellipse)) {
                    return false;
                }

                Ellipse targetMark = (Ellipse) mark;

                DoubleMatrix1D relativePosition =
                        twoElementMatrix(
                                targetMark.getPosition().x() - getPosition().x(),
                                targetMark.getPosition().y() - getPosition().y());

                DoubleMatrix1D relativePositionSquared = relativePosition.copy();
                relativePositionSquared.assign(Functions.square); // NOSONAR
                double distance = relativePositionSquared.zSum();

                // Definitely outside
                return distance > Math.pow(getMaximumRadius() + targetMark.getMaximumRadius(), 2.0);
            };

    // START Configurable parameters
    /** The size of the shell, expressed as a ratio of the radius. */
    @Getter @Setter private double shell = 0.2;
    // END configurable parameters

    // START mark state
    @Getter private Point2d radii;

    @Getter private Orientation orientation = new Orientation2D();
    // END mark state

    // START internal objects
    private EllipsoidMatrixCalculator ellipsoidCalculator;
    private double shellInternal;
    private double shellExternal;
    private double shellExternalOut;

    private double shellInternalSquared;
    private double shellExternalSquared;
    private double shellExternalOutSquared;

    private double radiiShellMaxSquared;
    // END internal objects

    // Default Constructor
    public Ellipse() {
        super();

        this.radii = new Point2d();

        ellipsoidCalculator = new EllipsoidMatrixCalculator(NUMBER_DIMENSIONS);
    }

    // Copy Constructor
    public Ellipse(Ellipse source) {
        super(source);
        this.radii = new Point2d(source.radii);

        this.shell = source.shell;

        this.ellipsoidCalculator = new EllipsoidMatrixCalculator(source.ellipsoidCalculator);
        this.orientation = source.orientation;

        this.shellExternal = source.shellExternal;
        this.shellInternal = source.shellInternal;
        this.shellExternalOut = source.shellExternalOut;
        this.shellExternalSquared = source.shellExternalSquared;
        this.shellInternalSquared = source.shellInternalSquared;
        this.shellExternalOutSquared = source.shellExternalOutSquared;

        this.radiiShellMaxSquared = source.radiiShellMaxSquared;
    }

    @Override
    public String getName() {
        return "ellipsoid";
    }

    // Where is a point in relation to the current object
    @Override
    public final byte isPointInside(Point3i point) {

        if (point.distanceSquared(this.getPosition()) > radiiShellMaxSquared) {
            return FLAG_SUBMARK_NONE;
        }

        // It is permissible to mutate the point during calculation
        double x = point.x() - getPosition().x();
        double y = point.y() - getPosition().y();

        // We exit early if it's inside the internal shell
        double sum = getEllipseSum(x, y, ellipsoidCalculator.getEllipsoidMatrix());

        if (sum <= shellInternalSquared) {
            return FLAG_SUBMARK_REGION0;
        }

        if (sum <= 1) {
            return FLAG_SUBMARK_REGION1;
        }

        if (sum <= shellExternalSquared) {
            return FLAG_SUBMARK_REGION2;
        }

        if (sum <= shellExternalOutSquared) {
            return FLAG_SUBMARK_REGION3;
        }

        return FLAG_SUBMARK_NONE;
    }

    @Override
    public double volume(int regionID) {

        if (regionID == GlobalRegionIdentifiers.SUBMARK_INSIDE) {
            return areaForShell(1);
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL) {
            return areaForShell(shellExternal) - areaForShell(shellInternal);
        } else {
            assert false;
            return 0.0;
        }
    }

    // Circumference
    public double circumference(int regionID) {
        if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL) {
            return circumferenceUsingRamunjanApprox(
                    this.radii.x() * (1.0 + shell), this.radii.y() * (1.0 + shell));
        } else {
            return circumferenceUsingRamunjanApprox(this.radii.x(), this.radii.y());
        }
    }

    @Override
    public Ellipse duplicate() {
        return new Ellipse(this);
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s pos=%s %s vol=%e shell=%f",
                "Ellpsd", identifier(), strPos(), descriptionMarks(), volume(0), shell);
    }

    public void updateshell(double shell) {
        setShell(shell);
        updateAfterMarkChange();
    }

    public void setMarksExplicit(Point3d position, Orientation orientation, Point2d radii) {
        Preconditions.checkArgument(position.z() == 0, "non-zero z-value");
        super.setPosition(position);
        this.orientation = orientation;
        this.radii = radii;
        updateAfterMarkChange();
    }

    @Override
    public void setMarksExplicit(Point3d position) {
        setMarksExplicit(position, orientation, radii);
    }

    @Override
    public void setMarksExplicit(Point3d position, Orientation orientation) {
        setMarksExplicit(position, orientation, radii);
    }

    public void setMarksExplicit(Point3d position, Orientation orientation, Point3d radii) {
        setMarksExplicit(position, orientation, new Point2d(radii.x(), radii.y()));
    }

    @Override
    public BoundingBox box(Dimensions dimensions, int regionID) {

        DoubleMatrix1D boxMatrix = ellipsoidCalculator.getBoundingBoxMatrix().copy();

        if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL) {
            boxMatrix.assign(Functions.mult(shellExternalOut));
        }

        return BoundingBoxCalculator.boxFromBounds(getPosition(), boxMatrix, false, dimensions);
    }

    @Override
    public Optional<QuickOverlapCalculation> quickOverlap() {
        return Optional.of(quickOverlap);
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
    public void scale(ScaleFactor scaleFactor) throws CheckedUnsupportedOperationException {
        super.scale(scaleFactor);
        ScaleChecker.checkIdenticalXY(scaleFactor);
        this.radii.scale(scaleFactor.x());
        updateAfterMarkChange();
    }

    @Override
    public boolean equalsDeep(Mark mark) {

        if (!super.equalsDeep(mark)) {
            return false;
        }

        if (!(mark instanceof Ellipse)) {
            return false;
        }

        Ellipse target = (Ellipse) mark;

        if (!radii.equals(target.radii)) {
            return false;
        }

        return orientation.equals(target.orientation);
    }

    @Override
    public OverlayProperties generateProperties(Optional<Resolution> resolution) {
        OverlayProperties properties = super.generateProperties(resolution);

        properties.addDoubleAsString("Radius X (pixels)", radii.x());
        properties.addDoubleAsString("Radius Y (pixels)", radii.y());
        orientation.describeOrientation(properties.getMap()::add);
        properties.addDoubleAsString("Shell Radius (pixels)", shell);

        return properties;
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
    public double[] createRadiiArrayResolved(Optional<Resolution> resolution) {
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

    private String descriptionMarks() {
        return String.format(
                "rad=[%3.3f, %3.3f] rot=%s", this.radii.x(), this.radii.y(), this.orientation);
    }

    private double areaForShell(double multiplier) {
        return (Math.PI * this.radii.x() * this.radii.y() * Math.pow(multiplier, 2));
    }

    private double getMaximumRadius() {
        return ellipsoidCalculator.getMaximumRadius();
    }

    private void updateAfterMarkChange() {

        assert (shell > 0);

        DoubleMatrix2D matRot = orientation.getRotationMatrix().getMatrix();

        double[] radiusArray = twoElementArray(this.radii.x(), this.radii.y());
        this.ellipsoidCalculator.update(radiusArray, matRot);

        this.shellInternal = 1.0 - this.shell;
        this.shellExternal = 1.0 + this.shell;
        this.shellExternalOut = 1.0 + (this.shell * 2);

        this.shellInternalSquared = squared(shellInternal);
        this.shellExternalSquared = squared(shellExternal);
        this.shellExternalOutSquared = squared(shellExternalOut);
        this.radiiShellMaxSquared = squared(ellipsoidCalculator.getMaximumRadius() * shellExternal);

        assert (!Double.isNaN(this.ellipsoidCalculator.getEllipsoidMatrix().get(0, 0)));
    }

    private static double circumferenceUsingRamunjanApprox(double a, double b) {
        // http://www.mathsisfun.com/geometry/ellipse-perimeter.html

        double first = 3 * (a + b);
        double second = ((3 * a) + b) * (a + (3 * b));

        return Math.PI * (first - Math.sqrt(second));
    }

    private static double getEllipseSum(double x, double y, DoubleMatrix2D matrix) {
        return x * (x * matrix.get(0, 0) + y * matrix.get(1, 0))
                + y * (x * matrix.get(0, 1) + y * matrix.get(1, 1));
    }
}
