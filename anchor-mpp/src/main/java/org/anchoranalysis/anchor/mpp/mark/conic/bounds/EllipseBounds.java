/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.conic.bounds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.bean.bound.BoundUnitless;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.Orientation;
import org.anchoranalysis.image.orientation.Orientation2D;

@NoArgsConstructor
public class EllipseBounds extends EllipseBoundsWithoutRotation {

    /** */
    private static final long serialVersionUID = 5833714580114414447L;

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private Bound rotationAngle;
    // END BEAN PROPERTIES

    // Constructor
    public EllipseBounds(Bound radiusBnd) {
        super(radiusBnd);
        rotationAngle = new BoundUnitless(0, 2 * Math.PI);
    }

    // Copy Constructor
    public EllipseBounds(EllipseBounds src) {
        super(src);
        rotationAngle = src.rotationAngle.duplicate();
    }

    @Override
    public String getBeanDscr() {
        return String.format(
                "%s, radius=(%s), rotation=(%s)",
                getBeanName(), getRadius().toString(), rotationAngle.toString());
    }

    @Override
    public Orientation randomOrientation(
            RandomNumberGenerator randomNumberGenerator, ImageResolution res) {
        return new Orientation2D(
                getRotationAngle().resolve(res, false).randOpen(randomNumberGenerator));
    }
}
