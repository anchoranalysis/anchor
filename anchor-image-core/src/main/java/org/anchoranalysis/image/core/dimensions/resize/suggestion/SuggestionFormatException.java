package org.anchoranalysis.image.core.dimensions.resize.suggestion;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;

/**
 * An exception thrown by {@link ImageResizeSuggestionFactory} to indicate a format is invalid or unknown.
 * 
 * @author Owen Feehan
 *
 */
public class SuggestionFormatException extends AnchorFriendlyCheckedException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SuggestionFormatException(String message) {
        super(message);
    }
}
