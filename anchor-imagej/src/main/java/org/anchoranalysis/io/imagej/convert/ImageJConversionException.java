package org.anchoranalysis.io.imagej.convert;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;

/**
 * When conversion to ImageJ data/type is not possible, or an error occurs during conversion.
 *
 * @author Owen Feehan
 */
public class ImageJConversionException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Creates with a message.
     *
     * @param message the message for the exception
     */
    public ImageJConversionException(String message) {
        super(message);
    }
}
