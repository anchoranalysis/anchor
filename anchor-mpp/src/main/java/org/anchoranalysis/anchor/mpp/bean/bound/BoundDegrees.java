/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.bound;

import org.anchoranalysis.core.unit.AngleConversionUtilities;
import org.anchoranalysis.image.extent.ImageResolution;

//
//  An upper and lower bound in degrees which is converted
//   to radians when resolved
//
public class BoundDegrees extends BoundMinMax {

    /** */
    private static final long serialVersionUID = 1281361242890359356L;

    public BoundDegrees() {
        super(0, 360);
    }

    public BoundDegrees(BoundDegrees src) {
        super(src);
    }

    @Override
    public double getMinResolved(ImageResolution sr, boolean do3D) {
        return AngleConversionUtilities.convertDegreesToRadians(getMin());
    }

    @Override
    public double getMaxResolved(ImageResolution sr, boolean do3D) {
        return AngleConversionUtilities.convertDegreesToRadians(getMax());
    }

    @Override
    public Bound duplicate() {
        return new BoundDegrees(this);
    }
}
