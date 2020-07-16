/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.bound;

import lombok.NoArgsConstructor;
import org.anchoranalysis.image.extent.ImageResolution;

//
//  An upper and lower bound in degrees which is converted
//   to radians when resolved
//
@NoArgsConstructor
public class BoundPhysicalExtent extends BoundMinMax {

    /** */
    private static final long serialVersionUID = -5440824428055546445L;

    public BoundPhysicalExtent(double min, double max) {
        super(min, max);
    }

    public BoundPhysicalExtent(BoundPhysicalExtent src) {
        super(src);
    }

    @Override
    public double getMinResolved(ImageResolution sr, boolean do3D) {
        return getMin() / sr.min(do3D);
    }

    @Override
    public double getMaxResolved(ImageResolution sr, boolean do3D) {
        return getMax() / sr.min(do3D);
    }

    @Override
    public Bound duplicate() {
        return new BoundPhysicalExtent(this);
    }
}
