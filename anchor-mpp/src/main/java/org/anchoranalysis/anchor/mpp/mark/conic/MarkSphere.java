/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.conic;

import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.mark.Mark;

/**
 * A sphere
 *
 * @author Owen Feehan
 */
public class MarkSphere extends MarkSingleRadius {

    /** */
    private static final long serialVersionUID = -3526056946146656810L;

    // Sphere with default properties
    public MarkSphere() {}

    // Constructor
    public MarkSphere(Bound boundRadius) {
        super(boundRadius);
    }

    // Copy Constructor - we do not copy scene
    public MarkSphere(MarkSphere src) {
        super(src);
    }

    @Override
    public String getName() {
        return "sphere";
    }

    @Override
    public double volume(int regionID) {
        double radiusCubed = Math.pow(radiusForRegion(regionID), 3.0);
        return (4 * Math.PI * radiusCubed) / 3;
    }

    @Override
    public String toString() {
        return String.format("%s %s pos=%s %s", "Sphr", strId(), strPos(), strMarks());
    }

    // The duplicate operation for the marks (we avoid clone() in case its confusing, we might not
    // always shallow copy)
    @Override
    public Mark duplicate() {
        return new MarkSphere(this);
    }

    @Override
    public int numDims() {
        return 3;
    }
}
