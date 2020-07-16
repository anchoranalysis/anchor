/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.conic.bounds;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.bean.bound.BoundUnitless;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.Orientation;
import org.anchoranalysis.image.orientation.Orientation3DEulerAngles;

/**
 * Creates a randomly-generated orientation in 3D based upon Euler Angles
 *
 * @author Owen Feehan
 */
public class RotationBounds3D extends RotationBounds {

    private static final Bound DEFAULT_BOUND = new BoundUnitless(0, 2 * Math.PI);

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private Bound rotationX = DEFAULT_BOUND;

    @BeanField @Getter @Setter private Bound rotationY = DEFAULT_BOUND;

    @BeanField @Getter @Setter private Bound rotationZ = DEFAULT_BOUND;
    // END BEAN PROPERTIES

    @Override
    public Orientation randomOrientation(
            RandomNumberGenerator randomNumberGenerator, ImageResolution res) {
        return new Orientation3DEulerAngles(
                randomizeRot(rotationX, randomNumberGenerator, res),
                randomizeRot(rotationY, randomNumberGenerator, res),
                randomizeRot(rotationZ, randomNumberGenerator, res));
    }

    @Override
    public String getBeanDscr() {
        return String.format(
                "%s, rotation=(%f,%f,%f)",
                getBeanName(), getRotationX(), getRotationY(), rotationZ);
    }

    private static double randomizeRot(
            Bound bound, RandomNumberGenerator randomNumberGenerator, ImageResolution res) {
        return bound.resolve(res, true).randOpen(randomNumberGenerator);
    }
}
