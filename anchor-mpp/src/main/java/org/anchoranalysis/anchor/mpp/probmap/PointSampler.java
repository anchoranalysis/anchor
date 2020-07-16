/* (C)2020 */
package org.anchoranalysis.anchor.mpp.probmap;

import java.util.Optional;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageDimensions;

public interface PointSampler {

    Optional<Point3d> sample(RandomNumberGenerator randomNumberGenerator);

    public abstract ImageDimensions getDimensions();
}
