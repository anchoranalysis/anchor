package org.anchoranalysis.image.core.dimensions.resize.suggestion;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * A series of suggestions to resize images.
 */
@AllArgsConstructor @Value
class ScaleToSuggestion implements ImageResizeSuggestion {

    /** The suggested width to resize to. */
    private final Optional<Integer> width;
    
    /** The suggested height to resize to. */
    private final Optional<Integer> height;
    
    /** If true, the aspect ratio must be preserved. */
    private final boolean preserveAspectRatio;
}
