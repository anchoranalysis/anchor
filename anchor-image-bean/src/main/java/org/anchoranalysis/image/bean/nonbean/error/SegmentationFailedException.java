/* (C)2020 */
package org.anchoranalysis.image.bean.nonbean.error;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

public class SegmentationFailedException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = -5014516097016484634L;

    public SegmentationFailedException(String message) {
        super(message);
    }

    public SegmentationFailedException(String message, Throwable cause) {
        super(cause);
    }

    public SegmentationFailedException(Throwable cause) {
        super(cause);
    }
}
