/* (C)2020 */
package org.anchoranalysis.io.filepath.findmatching;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

public class FindFilesException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public FindFilesException(String message) {
        super(message);
    }

    public FindFilesException(String message, Throwable cause) {
        super(message, cause);
    }
}
