/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

public class OptTerminatedEarlyException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1311039251626279919L;

    public OptTerminatedEarlyException(String message, Throwable cause) {
        super(message, cause);
    }
}
