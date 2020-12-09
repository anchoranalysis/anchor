package org.anchoranalysis.image.core.dimensions.resize.suggestion;

import java.util.Optional;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.spatial.scale.ScaleFactor;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * How much to scale an image by in both X and Y directions.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor @Value
class ScaleFactorSuggestion implements ImageResizeSuggestion {

    private final double scaleFactor;

    @Override
    public ScaleFactor calculateScaleFactor(Optional<Dimensions> dimensionsToBeScaled) {
        return new ScaleFactor(scaleFactor,scaleFactor);
    }
}
