/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.conic.bounds;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.bean.bound.OrientableBounds;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.extent.ImageResolution;

@NoArgsConstructor
@AllArgsConstructor
public abstract class EllipseBoundsWithoutRotation extends OrientableBounds {

    /** */
    private static final long serialVersionUID = -1109615535786453388L;

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private Bound radius;
    // END BEAN PROPERTIES

    // Copy Constructor
    public EllipseBoundsWithoutRotation(EllipseBoundsWithoutRotation src) {
        super();
        radius = src.radius.duplicate();
    }

    // NB objects are scaled in pre-rotated position i.e. when aligned to axes
    public void scaleXY(double multFactor) {
        this.radius.scale(multFactor);
    }

    @Override
    public double getMinResolved(ImageResolution sr, boolean do3D) {
        return radius.getMinResolved(sr, do3D);
    }

    @Override
    public double getMaxResolved(ImageResolution sr, boolean do3D) {
        return radius.getMaxResolved(sr, do3D);
    }
}
