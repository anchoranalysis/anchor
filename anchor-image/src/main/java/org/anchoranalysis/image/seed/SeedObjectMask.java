/* (C)2020 */
package org.anchoranalysis.image.seed;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.interpolator.InterpolatorFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.scale.ScaleFactor;

@AllArgsConstructor
public class SeedObjectMask implements Seed {

    private ObjectMask object;

    @Override
    public void scaleXY(double scale) throws OperationFailedException {
        object =
                object.scale(
                        new ScaleFactor(scale),
                        InterpolatorFactory.getInstance().noInterpolation());
    }

    @Override
    public void flattenZ() {
        object = object.flattenZ();
    }

    @Override
    public Seed duplicate() {
        return new SeedObjectMask(object.duplicate());
    }

    @Override
    public void growToZ(int sz) {
        object = object.growToZ(sz);
    }

    @Override
    public ObjectMask createMask() {
        return object;
    }

    @Override
    public boolean equalsDeep(Seed other) {
        if (other instanceof SeedObjectMask) {
            SeedObjectMask otherCast = (SeedObjectMask) other;
            return object.equalsDeep(otherCast.object);
        } else {
            return false;
        }
    }
}
