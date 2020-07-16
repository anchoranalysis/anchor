/* (C)2020 */
package org.anchoranalysis.io.error;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

public class FilePathPrefixerException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public FilePathPrefixerException(String message) {
        super(message);
    }

    public FilePathPrefixerException(Throwable cause) {
        super(cause);
    }
}
