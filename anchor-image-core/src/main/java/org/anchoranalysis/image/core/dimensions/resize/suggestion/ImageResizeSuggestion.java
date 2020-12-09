package org.anchoranalysis.image.core.dimensions.resize.suggestion;

import java.util.Optional;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * A suggestion on how to resize an image.
 * 
 * @author Owen Feehan
 *
 */
public interface ImageResizeSuggestion {

    /**
     * Calculates the scaling factor.
     * 
     * @param dimensionsToBeScaled dimensions of the source image/entity that will be scaled, if
     *     they are known.
     * @return the scaling-factor to use
     */
    ScaleFactor calculateScaleFactor(Optional<Dimensions> dimensionsToBeScaled) throws OperationFailedException;
}
