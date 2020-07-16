/* (C)2020 */
package org.anchoranalysis.image.io;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

public class RasterIOException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = -2707818821166134477L;

    public RasterIOException(String string) {
        super(string);
    }

    public RasterIOException(Throwable exc) {
        super(exc);
    }

    public RasterIOException(String string, Throwable exc) {
        super(string, exc);
    }
}
