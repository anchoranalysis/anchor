package org.anchoranalysis.io.input.path;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

public class DerivePathException extends AnchorFriendlyCheckedException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public DerivePathException(String message) {
        super(message);
    }
    
    public DerivePathException(Throwable cause) {
        super(cause);
    }

    public DerivePathException(String message, Throwable cause) {
        super(message, cause);
    }    
}
