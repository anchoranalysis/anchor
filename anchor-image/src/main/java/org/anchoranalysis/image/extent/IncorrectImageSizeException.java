/* (C)2020 */
package org.anchoranalysis.image.extent;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class IncorrectImageSizeException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = -7881057360315158663L;

    public IncorrectImageSizeException(String string) {
        super(string);
    }

    public IncorrectImageSizeException(Exception exc) {
        super(exc);
    }
}
