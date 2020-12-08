package org.anchoranalysis.image.core.dimensions.resize.suggestion;

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
}
