/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.error;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

public class CheckException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public CheckException(String message) {
        super(message);
    }

    public CheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckException(Throwable cause) {
        super(cause);
    }
}
