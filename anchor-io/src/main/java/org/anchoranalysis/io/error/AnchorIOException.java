/* (C)2020 */
package org.anchoranalysis.io.error;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class AnchorIOException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public AnchorIOException(String message) {
        super(message);
    }

    public AnchorIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
