/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.conic;

import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.mark.Mark;

/**
 * A circle
 *
 * @author Owen Feehan
 */
public class MarkCircle extends MarkSingleRadius {

    /** */
    private static final long serialVersionUID = 8551900716243748046L;

    // Sphere with default properties
    public MarkCircle() {}

    // Constructor
    public MarkCircle(Bound boundRadius) {
        super(boundRadius);
    }

    // Copy Constructor - we do not copy scene
    public MarkCircle(MarkCircle src) {
        super(src);
    }

    @Override
    public String getName() {
        return "circle";
    }

    @Override
    public double volume(int regionID) {
        return (2 * Math.PI * radiusForRegionSquared(regionID));
    }

    @Override
    public String toString() {
        return String.format("%s %s pos=%s %s", "circle", strId(), strPos(), strMarks());
    }

    // The duplicate operation for the marks (we avoid clone() in case its confusing, we might not
    // always shallow copy)
    @Override
    public Mark duplicate() {
        return new MarkCircle(this);
    }

    @Override
    public int numDims() {
        return 2;
    }
}
