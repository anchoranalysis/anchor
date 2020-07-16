/* (C)2020 */
package org.anchoranalysis.io.output.bound;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

public class BindFailedException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public BindFailedException(Throwable cause) {
        super(cause);
    }
}
