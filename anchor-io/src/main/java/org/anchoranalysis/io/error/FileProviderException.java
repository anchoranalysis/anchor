/* (C)2020 */
package org.anchoranalysis.io.error;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

public class FileProviderException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public FileProviderException(String message) {
        super(message);
    }

    public FileProviderException(Throwable cause) {
        super(cause);
    }
}
