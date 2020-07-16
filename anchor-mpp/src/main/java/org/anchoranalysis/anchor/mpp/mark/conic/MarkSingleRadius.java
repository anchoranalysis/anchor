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

import static org.anchoranalysis.anchor.mpp.mark.conic.TensorUtilities.squared;

import java.io.Serializable;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkAbstractPosition;
import org.anchoranalysis.anchor.mpp.mark.QuickOverlapCalculation;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;

/** Base-class for a conic that has a single radius (circle, sphere etc.) */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MarkSingleRadius extends MarkAbstractPosition implements Serializable {

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
     * @param bonudRadius
     */
    protected MarkSingleRadius(Bound boundRadius) {
        this.boundRadius = boundRadius;
    }

    /**
     * Copy constructor
     *
     * @param src
     */
    protected MarkSingleRadius(MarkSingleRadius src) {
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
     */
    @Override
    public void scale(double multFactor) {
        super.scale(multFactor);

        if (this.boundRadius != null) {
            this.boundRadius = this.boundRadius.duplicate();
            this.boundRadius.scale(multFactor);
        }

        setRadius(this.radius * multFactor);
    }

    @Override
    public BoundingBox bbox(ImageDimensions bndScene, int regionID) {
        return BoundingBoxCalculator.bboxFromBounds(
                getPos(), radiusForRegion(regionID) + ADDED_TO_RADIUS, numDims() == 3, bndScene);
    }

    @Override
    public BoundingBox bboxAllRegions(ImageDimensions bndScene) {
        return bbox(bndScene, GlobalRegionIdentifiers.SUBMARK_SHELL);
    }

    // Where is a point in relation to the current object
    @Override
    public final byte evalPointInside(Point3d pt) {

        double distance = getPos().distanceSquared(pt);

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
                    MarkSingleRadius markCast = ((MarkSingleRadius) mark);
                    double radiusSum = MarkSingleRadius.this.radius + markCast.radius;
                    double distanceBetweenCenters =
                            MarkSingleRadius.this.getPos().distance(markCast.getPos());
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
    public int numRegions() {
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

        MarkSingleRadius trgt = (MarkSingleRadius) mark;
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
