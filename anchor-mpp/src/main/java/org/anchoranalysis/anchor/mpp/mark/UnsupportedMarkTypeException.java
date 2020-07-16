/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class UnsupportedMarkTypeException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = -5855318973518035098L;

    public UnsupportedMarkTypeException(String string) {
        super(string);
    }

    public UnsupportedMarkTypeException(Exception exc) {
        super(exc);
    }
}
