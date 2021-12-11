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

import static org.anchoranalysis.mpp.mark.conic.TensorUtilities.squared;

import java.io.Serializable;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CheckedUnsupportedOperationException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.bound.Bound;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkWithPosition;
import org.anchoranalysis.mpp.mark.QuickOverlapCalculation;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/** Base-class for a conic that has a single radius (circle, sphere etc.) */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MarkWithPositionAndSingleRadius extends MarkWithPosition
        implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    private static final byte FLAG_SUBMARK_NONE = RegionMembershipUtilities.flagForNoRegion();
    private static final byte FLAG_SUBMARK_INSIDE =
            RegionMembershipUtilities.flagForRegion(GlobalRegionIdentifiers.SUBMARK_INSIDE);
    private static final byte FLAG_SUBMARK_SHELL =
            RegionMembershipUtilities.flagForRegion(GlobalRegionIdentifiers.SUBMARK_SHELL);

    /** Added to the radius in every dimension when determining bounds */
    private static final double ADDED_TO_RADIUS = 0.5;

    private static final double SPHERE_EXTRA_RAD = 2;

    // START mark state
    @Getter private double radius;
    // END mark state

    private double radiusSq;
    private double radiusExtraSq;

    @Getter private Bound boundRadius;

    /**
     * Constructor with a bound on the radius
     *
     * @param boundRadius
     */
    protected MarkWithPositionAndSingleRadius(Bound boundRadius) {
        this.boundRadius = boundRadius;
    }

    /**
     * Copy constructor
     *
     * @param src
     */
    protected MarkWithPositionAndSingleRadius(MarkWithPositionAndSingleRadius src) {
        super(src);
        this.boundRadius = src.boundRadius;
        this.radius = src.radius;
        this.radiusSq = src.radiusSq;
        this.radiusExtraSq = src.radiusExtraSq;
    }

    /**
     * Objects are scaled in pre-rotated position.
     *
     * <p>So when aligned to axes, we actually scale in all 3 dimensions, and ignore
     * scene-resolution
     *
     * @throws CheckedUnsupportedOperationException
     */
    @Override
    public void scale(ScaleFactor scaleFactor) throws CheckedUnsupportedOperationException {
        super.scale(scaleFactor);

        ScaleChecker.checkIdenticalXY(scaleFactor);

        if (this.boundRadius != null) {
            this.boundRadius = this.boundRadius.duplicate();
            this.boundRadius.scale(scaleFactor.x());
        }

        setRadius(this.radius * scaleFactor.x());
    }

    @Override
    public BoundingBox box(Dimensions dimensions, int regionID) {
        return BoundingBoxCalculator.boxFromBounds(
                getPosition(),
                radiusForRegion(regionID) + ADDED_TO_RADIUS,
                numberDimensions() == 3,
                dimensions);
    }

    @Override
    public BoundingBox boxAllRegions(Dimensions dimensions) {
        return box(dimensions, GlobalRegionIdentifiers.SUBMARK_SHELL);
    }

    // Where is a point in relation to the current object
    @Override
    public final byte isPointInside(Point3i point) {

        double distance = getPosition().distanceSquared(point);

        if (distance <= radiusSq) {
            return FLAG_SUBMARK_INSIDE;
        } else if (distance <= (radiusExtraSq)) {
            return FLAG_SUBMARK_SHELL;
        }

        return FLAG_SUBMARK_NONE;
    }

    private transient QuickOverlapCalculation quickOverlap =
            (Mark mark, int regionID) -> {
                if (getClass().equals(mark.getClass())) {
                    MarkWithPositionAndSingleRadius markCast =
                            ((MarkWithPositionAndSingleRadius) mark);
                    double radiusSum =
                            MarkWithPositionAndSingleRadius.this.radius + markCast.radius;
                    double distanceBetweenCenters =
                            MarkWithPositionAndSingleRadius.this
                                    .getPosition()
                                    .distance(markCast.getPosition());
                    return radiusSum > distanceBetweenCenters;
                } else {
                    throw new UnsupportedOperationException();
                }
            };

    @Override
    public Optional<QuickOverlapCalculation> quickOverlap() {
        return Optional.of(quickOverlap);
    }

    @Override
    public int numberRegions() {
        return 2;
    }

    @Override
    public boolean equalsDeep(Mark mark) {

        if (!super.equalsDeep(mark)) {
            return false;
        }

        if (!mark.getClass().equals(getClass())) {
            return false;
        }

        MarkWithPositionAndSingleRadius trgt = (MarkWithPositionAndSingleRadius) mark;
        return radius == trgt.radius;
    }

    public String strMarks() {
        return String.format("rad=%8.3f", this.radius);
    }

    public void setRadius(double radius) {
        this.radius = radius;

        this.radiusSq = squared(radius);
        this.radiusExtraSq = squared(radius + SPHERE_EXTRA_RAD);
    }

    protected double radiusForRegion(int regionID) {
        return regionID == GlobalRegionIdentifiers.SUBMARK_INSIDE
                ? radius
                : radius + SPHERE_EXTRA_RAD;
    }

    protected double radiusForRegionSquared(int regionID) {
        return regionID == GlobalRegionIdentifiers.SUBMARK_INSIDE ? radiusSq : radiusExtraSq;
    }
}
