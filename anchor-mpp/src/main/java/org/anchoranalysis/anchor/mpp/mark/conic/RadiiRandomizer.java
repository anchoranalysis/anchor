/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.conic;

import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageResolution;

/**
 * Utility functions for generating random radii for {@link MarkConic}
 *
 * @author Owen Feehan
 */
public class RadiiRandomizer {

    private RadiiRandomizer() {}

    public static Point3d randomizeRadii(
            Bound bound,
            RandomNumberGenerator randomNumberGenerator,
            ImageResolution sr,
            boolean do3D) {
        return new Point3d(
                randomizeRadius(bound, randomNumberGenerator, sr),
                randomizeRadius(bound, randomNumberGenerator, sr),
                do3D ? randomizeRadius(bound, randomNumberGenerator, sr) : 0);
    }

    private static double randomizeRadius(
            Bound radiusBound, RandomNumberGenerator randomNumberGenerator, ImageResolution sr) {
        return radiusBound.resolve(sr, true).randOpen(randomNumberGenerator);
    }
}
